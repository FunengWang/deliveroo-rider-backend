package com.deliveroo.rider.scheduled;

import com.deliveroo.rider.entity.Account;
import com.deliveroo.rider.entity.Activity;
import com.deliveroo.rider.entity.Order;
import com.deliveroo.rider.pojo.DayOfWeek;
import com.deliveroo.rider.pojo.WorkingType;
import com.deliveroo.rider.repository.AccountRepository;
import com.deliveroo.rider.repository.ActivityRepository;
import com.deliveroo.rider.service.ActivityService;
import com.deliveroo.rider.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import static com.deliveroo.rider.util.Utils.*;

@Component
public class MockDataTask {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityService activityService;

    @Scheduled(cron = "0 */45 * * * *") // Run every 45 minutes
    @Transactional
    public void mockActivities() {
        Iterator<Account> iterator = accountRepository.findAll().iterator();
        while(iterator.hasNext()) {
            Account account = iterator.next();
            if(account.notExpired() && !account.isNewCreated() && !account.isMocked()){
                //未过期，不是新创建出来的账号，且未生成过数据 默认生成之前12个月的送单记录
                List<Activity> activities = activityService.generateMockedActivities(account, 12);
                account.setActivities(activities);
                account.setMocked(true);
                accountRepository.save(account);
            }
        }
    }

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    /**
     * 每日凌晨1点生成 前一日的送餐记录
     * 模拟账号每日的送餐记录
     */
    public void mockDailyActivity(){
        Iterator<Account> iterator = accountRepository.findAll().iterator();
        while(iterator.hasNext()) {
            Account account = iterator.next();
            if(account.notExpired() && !account.isNewCreated() ) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                WorkingType workingType = account.getWorkingType();
                Activity activity = new Activity();
                activity.setDate(Utils.convertToLocalDate(calendar.getTime()));
                activity.setAccount(account);
                List<Order> orders = generateDailyOrders(calendar, workingType, activity);
                if(!orders.isEmpty()) {
                    activity.setOrders(orders);
                }
                activityRepository.save(activity);
            }
        }
    }

    private List<Order> generateDailyOrders(Calendar calendar, WorkingType workingType, Activity activity){
        DayOfWeek dayOfWeek = Utils.mapToDayOfWeek(calendar);
        List<Order> orders = new ArrayList<>();
        if(workingType == WorkingType.BUSY){
            orders = activityService.randomOrders(generateRandomNumber(10,15),dayOfWeek,activity);
        } else if(workingType == WorkingType.NORMAL){
            if(dayOfWeek != DayOfWeek.SUNDAY){
                orders = activityService.randomOrders(generateRandomNumber(5,10),dayOfWeek,activity);
            }
        } else {
            if(dayOfWeek != DayOfWeek.SUNDAY && dayOfWeek!= DayOfWeek.SATURDAY){
                orders = activityService.randomOrders(generateRandomNumber(2,5),dayOfWeek,activity);
            }
        }
        return orders;
   }
}
