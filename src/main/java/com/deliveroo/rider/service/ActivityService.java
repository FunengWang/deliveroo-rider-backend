package com.deliveroo.rider.service;

import com.deliveroo.rider.controller.ActivityController;
import com.deliveroo.rider.entity.*;
import com.deliveroo.rider.pojo.DayOfWeek;
import com.deliveroo.rider.pojo.WorkingType;
import com.deliveroo.rider.repository.ActivityRepository;
import com.deliveroo.rider.repository.FeeBoostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.deliveroo.rider.util.Utils.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.deliveroo.rider.util.Utils.mapToDayOfWeek;

@Service
public class ActivityService {
    @Autowired
    private ActivityRepository repository;

    @Autowired
    private FeeBoostRepository feeBoostRepository;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    private static final String PLACES_KEY = "places";
    private static final String SHOPS_KEY = "shops";

    private static volatile List<FeeBoost> FEEBOOSTLIST;

    /**
     * the default working type is normal, default months is 6 months
     * Busy working type work 7 days per week,                              15 orders in average per day,
     * normal working type work from monday to Saturday, be off on Sundays, 10 orders in average per day,
     * easy working type, work from monday to Friday, be off at weekends, 5 orders in average per day,
     *
     */
    @Transactional
    public void generateMockedActivity(Account account, Integer months) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -months);
        Calendar now = Calendar.getInstance();
        ArrayList<Activity> bufferActivities = new ArrayList<>();
        WorkingType workingType = account.getWorkingType();
        while (true) {
            switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                case 1:
                    //sunday
                    if (workingType == WorkingType.BUSY) {
                        bufferActivities.add(randomActivity(calendar, account));
                    }
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    bufferActivities.add(randomActivity(calendar, account));
                    break;
                case 7:
                    //Saturday
                    if (workingType == WorkingType.BUSY || workingType == WorkingType.NORMAL) {
                        bufferActivities.add(randomActivity(calendar, account));
                    }
                    break;
            }
            if (bufferActivities.size() == 20) {
                repository.saveAll(bufferActivities);
                bufferActivities.clear();
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                    calendar.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                    calendar.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)) {
                repository.saveAll(bufferActivities);
                bufferActivities.clear();
                break;
            }
        }
    }

    private Activity randomActivity(Calendar calendar, Account account) {
        Activity activity = new Activity();
        activity.setDate(calendar.getTime());
        activity.setAccount(account);
        DayOfWeek dayOfWeek = mapToDayOfWeek(calendar);
        WorkingType workingType = account.getWorkingType();
        switch (workingType) {
            case BUSY:
                activity.setOrders(randomOrders(15, dayOfWeek));
            case NORMAL:
                activity.setOrders(randomOrders(10, dayOfWeek));
            case EASY:
                activity.setOrders(randomOrders(5, dayOfWeek));

        }
        return activity;
    }

    private List<Order> randomOrders(int maxOrders, DayOfWeek dayOfWeek) {
        List<Order> orders = new ArrayList<>();
        for (int i = 1; i <= maxOrders; i++) {
            orders.add(randomOrder(dayOfWeek));
        }
        return orders;
    }

    private Order randomOrder(DayOfWeek dayOfWeek) {
        Order order = new Order();
        order.setFee(randomFee(dayOfWeek));
        order.setOrderDetails(randomOrderDetails());
        order.setTip(randomTip(1, 5));
        order.setShop(randomShop());
        order.setPlace(randomPlace());
        order.setExtra(randomExtra(dayOfWeek, order));
        return order;
    }

    private Double randomExtra(DayOfWeek dayOfWeek, Order order) {
        if (FEEBOOSTLIST == null) {
            searchFeeBoostList();
        }
        Optional<FeeBoost> optional = FEEBOOSTLIST.stream()
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

    private List<OrderDetail> randomOrderDetails() {
        List<OrderDetail> orderDetails = new ArrayList<>();
        OrderDetail orderDetail = randomOrderDetail();
        orderDetails.add(orderDetail);
        Random random = new Random();
        if (random.nextInt(10) > 8.5) {
            orderDetails.add(randomOrderDetail(orderDetail.getStart(), orderDetail.getComplete()));
        }
        return orderDetails;
    }

    private OrderDetail randomOrderDetail() {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderNo(randomOrderNo());
        int startHour = randomStartHour();
        int startMinute = randomStartMinute();
        int minutes = randomTimeElapsed(5, 45);
        orderDetail.setStart(calculateStartTime(startHour, startMinute));
        orderDetail.setComplete(calculateCompleteTime(startHour, startMinute + minutes));
        return orderDetail;
    }

    private OrderDetail randomOrderDetail(LocalTime start, LocalTime complete) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderNo(randomOrderNo());
        int minutes = randomTimeElapsed(5, 15);
        orderDetail.setStart(start);
        orderDetail.setComplete(calculateCompleteTime(complete.getHour(), complete.getMinute() + minutes));
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

    private LocalTime calculateCompleteTime(int hour, int minute) {
        if (minute > 59) {
            hour++;
            minute -= 60;
        }
        if (hour > 23) {
            hour -= 24;
        }
        return LocalTime.of(hour, minute);
    }

    private LocalTime calculateStartTime(int hour, int minute) {
        return LocalTime.of(hour, minute);
    }

    private void searchFeeBoostList() {
        if (FEEBOOSTLIST == null) {
            synchronized (ActivityController.class) {
                if (FEEBOOSTLIST == null) {
                    Iterable<FeeBoost> feeBoosts = feeBoostRepository.findAll();
                    FEEBOOSTLIST = new ArrayList<>();
                    for (FeeBoost next : feeBoosts) {
                        FEEBOOSTLIST.add(next);
                    }
                }
            }
        }
    }

    private boolean ifInExtraPeriod(FeeBoost feeBoost, List<OrderDetail> orderDetails) {
        return orderDetails.stream().anyMatch(ele ->
                ele.getStart().isAfter(feeBoost.getStart()) &&
                        ele.getComplete().isBefore(feeBoost.getComplete()));
    }
}
