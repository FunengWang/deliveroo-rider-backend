package com.deliveroo.rider;

import com.deliveroo.rider.entity.Account;
import com.deliveroo.rider.entity.Activity;
import com.deliveroo.rider.entity.Order;
import com.deliveroo.rider.entity.OrderDetail;
import com.deliveroo.rider.pojo.AccountType;
import com.deliveroo.rider.repository.AccountRepository;
import com.deliveroo.rider.repository.ActivityRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@RunWith(SpringRunner.class)
public class JpaRepositoryTest {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Test
    @Transactional
    public void testJpaRepositoryOperation() {
        assertNotNull(accountRepository);
        Account account = mockAccount();
        Account saved = accountRepository.save(account);
        List<Activity> activities = saved.getActivities();
        assertNotNull(activities);
        assertEquals(activities.size(), 2);

        for (Activity activity : activities) {
            List<Order> orders = activity.getOrders();
            assertNotNull(orders);
            assertEquals(orders.size(), 2);

            for (Order order : orders) {
                List<OrderDetail> orderDetails = order.getOrderDetails();
                assertNotNull(orderDetails);
                assertEquals(orderDetails.size(), 2);
            }
        }


        /**
         * 测试,s双向一对多结构，由多的一方维护外键，从一方插入复杂数据，hibernate会如何生成SQl
         * public class Account {
         *     @OneToMany(mappedBy = "account",cascade = CascadeType.ALL)
         *     private List<Activity> activities;
         * }
         * public class Activity {
         *     @ManyToOne
         *     @JoinColumn(name = "account_id")
         *     private Account account;
         *
         *     @OneToMany(mappedBy = "activity",cascade = CascadeType.ALL)
         *     private List<Order> orders;
         * }
         * public class Order {
         *     @ManyToOne
         *     @JoinColumn(name = "activity_id")
         *     private Activity activity;
         *
         *     @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
         *     private List<OrderDetail> orderDetails;
         * }
         *
         * public class OrderDetail {
         *     @ManyToOne
         *     @JoinColumn(name = "order_id")
         *     private Order order;
         *}
         * Hibernate: insert into rider_account (account_type, calling_code, city, country, email, expiration_date, first_name, lastname, phone, rider_id, security_code, working_type) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
         * Hibernate: insert into activity (account_id, date) values (?, ?)
         * Hibernate: insert into delivery_order (activity_id, extra, fee, place, shop, tip) values (?, ?, ?, ?, ?, ?)
         * Hibernate: insert into order_detail (complete, order_id, order_no, start) values (?, ?, ?, ?)
         * Hibernate: insert into order_detail (complete, order_id, order_no, start) values (?, ?, ?, ?)
         * Hibernate: insert into delivery_order (activity_id, extra, fee, place, shop, tip) values (?, ?, ?, ?, ?, ?)
         * Hibernate: insert into order_detail (complete, order_id, order_no, start) values (?, ?, ?, ?)
         * Hibernate: insert into order_detail (complete, order_id, order_no, start) values (?, ?, ?, ?)
         * Hibernate: insert into activity (account_id, date) values (?, ?)
         * Hibernate: insert into delivery_order (activity_id, extra, fee, place, shop, tip) values (?, ?, ?, ?, ?, ?)
         * Hibernate: insert into order_detail (complete, order_id, order_no, start) values (?, ?, ?, ?)
         * Hibernate: insert into order_detail (complete, order_id, order_no, start) values (?, ?, ?, ?)
         * Hibernate: insert into delivery_order (activity_id, extra, fee, place, shop, tip) values (?, ?, ?, ?, ?, ?)
         * Hibernate: insert into order_detail (complete, order_id, order_no, start) values (?, ?, ?, ?)
         * Hibernate: insert into order_detail (complete, order_id, order_no, start) values (?, ?, ?, ?)
         * Hibernate: select activity0_.id as id1_0_0_, orders1_.id as id1_1_1_, activity0_.account_id as account_3_0_0_, activity0_.date as date2_0_0_, orders1_.activity_id as activity7_1_1_, orders1_.extra as extra2_1_1_, orders1_.fee as fee3_1_1_, orders1_.place as place4_1_1_, orders1_.shop as shop5_1_1_, orders1_.tip as tip6_1_1_, orders1_.activity_id as activity7_1_0__, orders1_.id as id1_1_0__ from activity activity0_ left outer join delivery_order orders1_ on activity0_.id=orders1_.activity_id where activity0_.account_id=?
         *
         */
        Optional<Account> optionalAccount = accountRepository.findById(saved.getId());
        Account account1 = optionalAccount.get();
        List<Activity> activities1 = activityRepository.findByAccount(account1);
        assertNotNull(activities1);
        assertEquals(activities1.size(), 2);

        for (Activity activity : activities1) {
            List<Order> orders = activity.getOrders();
            assertNotNull(orders);
            assertEquals(orders.size(), 2);

            for (Order order : orders) {
                List<OrderDetail> orderDetails = order.getOrderDetails();
                assertNotNull(orderDetails);
                assertEquals(orderDetails.size(), 2);
            }
        }
    }

    private Account mockAccount() {
        Account account = new Account();
        account.setPhone("838590817");
        account.setEmail("funengwang94@gmail.com");
        account.setAccountType(AccountType.E_BIKE);
        account.setSecurityCode("606029");
        account.setRiderId("834039");
        account.setFirstName("Funeng");
        account.setLastname("Wang");
        account.setExpirationDate(LocalDateTime.now());
        account.setActivities(mockActivities(account));
        return account;
    }

    private List<Activity> mockActivities(Account account) {
        List<Activity> activities = new ArrayList<>();
        activities.add(mockActivity(account));
        activities.add(mockActivity(account));
        return activities;
    }

    private Activity mockActivity(Account account) {
        Activity activity = new Activity();
        activity.setDate(LocalDate.now());
        activity.setAccount(account);
        activity.setOrders(mockOrders(activity));
        return activity;
    }

    private List<Order> mockOrders(Activity activity) {
        List<Order> orders = new ArrayList<>();
        orders.add(mockOrder(activity));
        orders.add(mockOrder(activity));
        return orders;
    }

    private Order mockOrder(Activity activity) {
        Order order = new Order();
        order.setShop("Subway");
        order.setPlace("GAL");
        order.setFee(6.3);
        order.setActivity(activity);
        order.setOrderDetails(mockOrderDetails(order));
        return order;
    }

    private List<OrderDetail> mockOrderDetails(Order order) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        orderDetails.add(mockOrderDetail(order));
        orderDetails.add(mockOrderDetail(order));
        return orderDetails;
    }

    private OrderDetail mockOrderDetail(Order order) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderNo("xxxxx");
        orderDetail.setStart(LocalTime.of(10, 30));
        orderDetail.setComplete(LocalTime.of(11, 30));
        orderDetail.setOrder(order);
        return orderDetail;
    }
}
