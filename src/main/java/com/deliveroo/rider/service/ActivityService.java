package com.deliveroo.rider.service;

import com.deliveroo.rider.configuration.JwtTokenProvider;
import com.deliveroo.rider.entity.*;
import com.deliveroo.rider.pojo.DayOfWeek;
import com.deliveroo.rider.pojo.Month;
import com.deliveroo.rider.pojo.WorkingType;
import com.deliveroo.rider.pojo.dto.DailyActivity;
import com.deliveroo.rider.pojo.dto.OrderSummary;
import com.deliveroo.rider.pojo.dto.PlaceSummary;
import com.deliveroo.rider.repository.AccountRepository;
import com.deliveroo.rider.repository.ActivityRepository;
import com.deliveroo.rider.repository.OrderDetailRepository;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.deliveroo.rider.util.Constants.*;
import static com.deliveroo.rider.util.Utils.*;

@Service
public class ActivityService {
    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    /**
     * the default working type is normal, default months is 12 months
     * Busy working type work 7 days per week,                              12 orders in average per day,
     * normal working type work from monday to Saturday, be off on Sundays, 8 orders in average per day,
     * easy working type, work from monday to Friday, be off at weekends, 4 orders in average per day,
     *
     */
    public List<Activity> generateMockedActivities(Account account, Integer months) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -months);
        Calendar now = Calendar.getInstance();
        ArrayList<Activity> randomActivities = new ArrayList<>();
        WorkingType workingType = account.getWorkingType();
        while (true) {
            switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                case 1:
                    //sunday
                    if (workingType == WorkingType.BUSY) {
                        randomActivities.add(randomActivity(calendar, account));
                    }
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    randomActivities.add(randomActivity(calendar, account));
                    break;
                case 7:
                    //Saturday
                    if (workingType == WorkingType.BUSY || workingType == WorkingType.NORMAL) {
                        randomActivities.add(randomActivity(calendar, account));
                    }
                    break;
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                    calendar.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                    calendar.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)) {
                break;
            }
        }
        return randomActivities;
    }

    public List<Activity> getActivities(Account account, LocalDate from, LocalDate to){
        return activityRepository.findByAccount(account)
                .stream()
                .filter(activity ->
                        (activity.getDate().isBefore(to) && activity.getDate().isAfter(from)) ||
                                activity.getDate().isEqual(to) ||
                                activity.getDate().isEqual(from))
                .collect(Collectors.toList());
    }

    public List<Activity> getActivities(Account account, int year, Month month) {
        return activityRepository.findByAccount(account).stream()
                .filter(activity->
                        activity.getDate().getYear() == year &&
                                activity.getDate().getMonthValue() == month.ordinal()+1)
                .collect(Collectors.toList());
    }

    public Account getAccountByToken (String token){
        Claims claims = tokenProvider.parseToken(token);
        Long accountId =  Long.parseLong(claims.get("accountId",String.class));
        Optional<Account> optionalAccount = accountRepository.findById(accountId);
        return optionalAccount.orElse(null);
    }

    public DailyActivity generateDailyActivity(Activity activity){
        DailyActivity dailyActivity = new DailyActivity();
        dailyActivity.setId(activity.getId());
        dailyActivity.setDate(activity.getDate());
        dailyActivity.setOrders(calculateTotalOrders(activity));
        dailyActivity.setFees(calculateTotalFees(activity));
        dailyActivity.setExtraFees(calculateTotalExtraFees(activity));
        dailyActivity.setTips(calculateTotalTips(activity));
        dailyActivity.setDailyEarnings(calculateTotalEarnings(activity));
        dailyActivity.setPlaceSummaries(generatePlaceSummaries(activity.getOrders()));
        return dailyActivity;
    }

    private List<PlaceSummary> generatePlaceSummaries(List<Order> orders){
        List<PlaceSummary> placeSummaries = new ArrayList<>();
        List<OrderSummary> orderSummaries = generateOrderSummaries(orders);
        Map<String, List<OrderSummary>> map = orderSummaries
                .stream()
                .collect(Collectors.groupingBy(OrderSummary::getPlace));
        Iterator<Map.Entry<String, List<OrderSummary>>> iterator = map.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, List<OrderSummary>> entry = iterator.next();
            PlaceSummary placeSummary = new PlaceSummary();
            placeSummary.setPlace(entry.getKey());
            placeSummary.setOrderSummaries(entry.getValue());
            placeSummaries.add(placeSummary);
        }
        return placeSummaries;
    }

    private List<OrderSummary> generateOrderSummaries(List<Order> orders) {
        List<OrderSummary> orderSummaries = new ArrayList<>();
        for (Order order : orders) {
            OrderSummary orderSummary = new OrderSummary();
            orderSummary.setId(order.getId());
            orderSummary.setPlace(order.getPlace());
            orderSummary.setShop(order.getShop());
            orderSummary.setEarnings(order.getEarnings());
            orderSummary.setHasSubOrder(order.hasSubOrder());
            orderSummary.setStart(order.getStartTime());
            orderSummary.setComplete(order.getCompleteTime());
            orderSummaries.add(orderSummary);
        }
        orderSummaries.sort(Comparator.comparing(ele->ele.getStart()));
        return orderSummaries;
    }

    private Activity randomActivity(Calendar calendar, Account account) {
        Activity activity = new Activity();
        ZonedDateTime zonedDateTime = calendar.toInstant().atZone(ZoneId.systemDefault());
        activity.setDate(zonedDateTime.toLocalDate());
        activity.setAccount(account);
        DayOfWeek dayOfWeek = mapToDayOfWeek(calendar);
        WorkingType workingType = account.getWorkingType();
        switch (workingType) {
            case BUSY:
                activity.setOrders(randomOrders(generateRandomNumber(10,15), dayOfWeek, activity));
                break;
            case NORMAL:
                activity.setOrders(randomOrders(generateRandomNumber(5,10), dayOfWeek, activity));
                break;
            case EASY:
                activity.setOrders(randomOrders(generateRandomNumber(2,5), dayOfWeek, activity));
                break;
        }
        return activity;
    }

    public List<Order> randomOrders(int maxOrders, DayOfWeek dayOfWeek, Activity activity) {
        List<Order> orders = new ArrayList<>();
        for (int i = 1; i <= maxOrders; i++) {
            orders.add(randomOrder(dayOfWeek,activity));
        }
        return orders;
    }

    private Order randomOrder(DayOfWeek dayOfWeek, Activity activity) {
        Order order = new Order();
        order.setFee(randomFee(dayOfWeek));
        order.setOrderDetails(randomOrderDetails(order));
        order.setTip(randomTip(1, 5));
        order.setShop(randomShop());
        order.setPlace(randomPlace());
        order.setExtra(randomExtra(dayOfWeek, order));
        order.setActivity(activity);
        return order;
    }

    private Double randomExtra(DayOfWeek dayOfWeek, Order order) {
        BoundSetOperations<String, Object> set = redisTemplate.boundSetOps(FEE_BOOSTS_KEY);
        List<FeeBoost> feeBoostList = convertToFeeBoosts(set.members());
        Optional<FeeBoost> optional = feeBoostList.stream()
                .filter(item -> item.getDayOfWeek() == dayOfWeek)
                .findFirst();
        if (optional.isPresent()) {
            FeeBoost feeBoost = optional.get();
            if (ifInExtraPeriod(feeBoost, order.getOrderDetails())) {
                BigDecimal result = BigDecimal.valueOf(order.getFee())
                        .multiply(BigDecimal.valueOf(feeBoost.getRate() - 1));
                return formatDouble(result.doubleValue());
            }
        }
        return 0.0;
    }

    private Double randomTip(int min, int max) {
        if (Math.random() > 0.87) {
            Random random = new Random();
            return formatDouble(min + (max - min) * random.nextDouble());
        }
        return 0.0;
    }

    private Double randomFee(DayOfWeek dayOfWeek) {
        double random;
        if (dayOfWeek == DayOfWeek.FRIDAY || dayOfWeek == DayOfWeek.SATURDAY) {
            random = 2.9 + (Math.random() * (14 - 2.9));
        } else {
            random = 2.9 + (Math.random() * (9 - 2.9));
        }
        return formatDouble(random);
    }

    private List<OrderDetail> randomOrderDetails(Order order) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        OrderDetail orderDetail = randomOrderDetail(order);
        orderDetail.setOrder(order);
        orderDetails.add(orderDetail);
        Random random = new Random();
        if (random.nextInt(10) > 8.5) {
            orderDetails.add(randomOrderDetail(orderDetail.getStart(), orderDetail.getComplete(), order));
        }
        return orderDetails;
    }

    private OrderDetail randomOrderDetail(Order order) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderNo(randomOrderNo());
        orderDetail.setOrder(order);
        int startHour = randomStartHour();
        int startMinute = randomStartMinute();
        int minutes = randomTimeElapsed(5, 45);
        orderDetail.setStart(calculateStartTime(startHour, startMinute));
        orderDetail.setComplete(calculateCompleteTime(startHour, startMinute + minutes));
        return orderDetail;
    }

    private OrderDetail randomOrderDetail(LocalTime start, LocalTime complete, Order order) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderNo(randomOrderNo());
        int minutes = randomTimeElapsed(5, 15);
        orderDetail.setStart(start);
        orderDetail.setComplete(calculateCompleteTime(complete.getHour(), complete.getMinute() + minutes));
        orderDetail.setOrder(order);
        return orderDetail;
    }

    private String randomOrderNo() {
        int random = (int) (Math.random() * 10000);
        if (random < 100) {
            return "00" + random;
        } else if (random < 1000) {
            return "0" + random;
        } else {
            return String.valueOf(random);
        }
    }

    private String randomShop() {
        List<String> shops = redisTemplate.opsForList().range(SHOPS_KEY, 0, -1)
                .stream()
                .map(ele->ele.toString())
                .collect(Collectors.toList());
        int index = new Random().nextInt(shops.size());
        return shops.get(index);
    }

    private String randomPlace() {
        List<String> places = redisTemplate.opsForList().range(PLACES_KEY, 0, -1)
                .stream()
                .map(ele->ele.toString())
                .collect(Collectors.toList());
        int index = new Random().nextInt(places.size());
        return places.get(index);
    }

    private int randomStartMinute() {
        Random random = new Random();
        return random.nextInt(59);
    }

    private int randomStartHour() {
        Random random = new Random();
        return random.nextInt(24);
    }

    private int randomTimeElapsed(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }



    private boolean ifInExtraPeriod(FeeBoost feeBoost, List<OrderDetail> orderDetails) {
        return orderDetails.stream().anyMatch(ele ->
                ele.getStart().isAfter(feeBoost.getStart()) &&
                        ele.getComplete().isBefore(feeBoost.getComplete()));
    }
}
