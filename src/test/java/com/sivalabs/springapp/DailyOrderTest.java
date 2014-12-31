package com.sivalabs.springapp;

import com.sivalabs.springapp.config.AppConfig;
import com.sivalabs.springapp.entities.DailyOrder;
import com.sivalabs.springapp.entities.User;
import com.sivalabs.springapp.repositories.DailyOrderRepository;
import com.sivalabs.springapp.services.DailyOrderService;
import com.sivalabs.springapp.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: giridhad
 * Date: 12/19/14
 * Time: 3:06 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class DailyOrderTest {
    @Autowired
    private UserService userService;
    @Autowired
    private DailyOrderService dailyOrderService;
    @Autowired
    private DailyOrderRepository dailyOrderRepository;

    private User user = null;

    @Before
    public void createUser() {
        String uuid = UUID.randomUUID().toString();
        user = new User();
        user.setEmail(uuid + "@gmail.com");
        user.setName("Some name");
        user.setSector("Sector");
        user.setPhone("1112223333");
        user.setActive(Boolean.TRUE);
        User savedUser = userService.create(user);
        assertNotNull("User just got saved there should be ID associated", user.getId());
        User newUser = userService.findUserById(savedUser.getId());
        assertEquals("Some name", newUser.getName());
        assertEquals(uuid + "@gmail.com", newUser.getEmail());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testDailyOrderInsert() {
        LocalDate localDate = LocalDate.of(2012, Month.FEBRUARY, 1);
        int noOfDays = localDate.getMonth().length(localDate.isLeapYear());
        for (int i = 1; i <= noOfDays; i++) {
            LocalDate dateOfMonth = LocalDate.of(localDate.getYear(), localDate.getMonth(), i);
            Instant instant = dateOfMonth.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
            DailyOrder dailyOrder = new DailyOrder();
            dailyOrder.setBmOrder(1.5);
            dailyOrder.setCmOrder(2.5);
            dailyOrder.setOrderDate(Date.from(instant));
            dailyOrder.setUser(user);
            dailyOrderRepository.save(dailyOrder);
        }
        List<DailyOrder> dailyOrders = dailyOrderService.findByUserId(user.getId());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testOrderDateBetween(){
        LocalDate localDate = LocalDate.of(2012, Month.FEBRUARY, 1);
        int noOfDays = localDate.getMonth().length(localDate.isLeapYear());
        for (int i = 1; i <= noOfDays; i++) {
            LocalDate dateOfMonth = LocalDate.of(localDate.getYear(), localDate.getMonth(), i);
            Instant instant = dateOfMonth.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
            DailyOrder dailyOrder = new DailyOrder();
            dailyOrder.setBmOrder(1.5);
            dailyOrder.setCmOrder(2.5);
            dailyOrder.setOrderDate(Date.from(instant));
            dailyOrder.setUser(user);
            dailyOrderRepository.save(dailyOrder);
        }
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.MONTH, localDate.getMonth().getValue() - 1);
        calendar.set(Calendar.YEAR, localDate.getYear());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date dateNotBefore = calendar.getTime();

        calendar.set(Calendar.MONTH, localDate.getMonth().getValue() - 1);
        calendar.set(Calendar.YEAR, localDate.getYear());
        calendar.set(Calendar.DAY_OF_MONTH, noOfDays);
        Date dateNotAfter = calendar.getTime();

        List<DailyOrder> dailyOrders = dailyOrderService.findByUserIdAndOrderDateBetween(user.getId(), dateNotBefore, dateNotAfter);
        Assert.isTrue(dailyOrders != null,"Daily orders cannot be null");
        Assert.isTrue(dailyOrders.size()==noOfDays,"Size of the return value should be equal to No. of days");
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testCreateDailyOrdersForAllActiveUsers(){
        dailyOrderService.createDailyOrdersForAllActiveUsers();
        List<DailyOrder> dailyOrders = dailyOrderService.findAllDailyOrders();
        Assert.notEmpty(dailyOrders);
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testCreateOrUpdateDailyOrder(){
        LocalDate dateOfMonth = LocalDate.now();
        Instant instant = dateOfMonth.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        DailyOrder dailyOrder = new DailyOrder();
        dailyOrder.setBmOrder(1.5);
        dailyOrder.setCmOrder(2.5);
        dailyOrder.setOrderDate(Date.from(instant));
        dailyOrder.setUser(user);
        dailyOrderRepository.save(dailyOrder);
        double oldBmOrder = dailyOrder.getBmOrder();
        double oldCmOrder = dailyOrder.getCmOrder();
        dailyOrderService.createOrUpdateDailyOrder((double) 2, (double) 2, Date.from(instant), user.getId());

        Assert.isTrue(dailyOrder.getBmOrder() != oldBmOrder);
        Assert.isTrue(dailyOrder.getCmOrder() != oldCmOrder);
    }
}
