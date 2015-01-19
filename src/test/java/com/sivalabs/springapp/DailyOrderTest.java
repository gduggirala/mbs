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

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        user.setOrderStartDate(new Date());
        user.setDailyCmOrder((double) 1);
        user.setDailyBmOrder((double) 1);
        user.setBmPrice((double) 55);
        user.setCmPrice(45d);
        user.setGivenSerialNumber(1 );
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
    public void testOrderDateBetween() {
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
        Assert.isTrue(dailyOrders != null, "Daily orders cannot be null");
        Assert.isTrue(dailyOrders.size() == noOfDays, "Size of the return value should be equal to No. of days");
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testCreateDailyOrdersForAllActiveUsers() throws ParseException {
        dailyOrderService.createDailyOrdersForAllActiveUsers();
        List<DailyOrder> dailyOrders = dailyOrderService.findAllDailyOrders();
        Assert.notEmpty(dailyOrders);
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testCreateOrUpdateDailyOrder() {
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

    @Test
    @Transactional
    @Rollback(true)
    public void testFinalizeDailyOrderForUser() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -3);
        user.setOrderStartDate(calendar.getTime());
        userService.update(user);
        dailyOrderService.createDailyOrderForUser(user);
        List<DailyOrder> dailyOrderList = dailyOrderService.findByUserId(user.getId());
        int initialLength = dailyOrderList.size();
        Assert.notEmpty(dailyOrderList, "Daily order list cannot be empty");
        dailyOrderService.finalizeDailyOrderForUser(user, new Date());
        List<DailyOrder> finalizedDailyOrderList = dailyOrderService.findByUserId(user.getId());
        int postLength = finalizedDailyOrderList.size();
        Assert.isTrue(postLength == initialLength, "Post length and initial length should be equal");
        calendar.add(Calendar.DAY_OF_MONTH, +6);
        Assert.notEmpty(finalizedDailyOrderList);
    }

    @Test
   // @Transactional
    public void testCreatePreviousMonthsDailyOrders() {
        List<User> userList = userService.findByIsActiveTrue();
        for (User user : userList) {
            LocalDate localDate1 = LocalDate.now();
            LocalDate localDate = localDate1.minusMonths(1);
            user.setOrderStartDate(DateUtils.asDate(localDate));
            int noOfDays = localDate.lengthOfMonth();
            if (user.getDailyBmOrder() != null && user.getDailyCmOrder() != null) {
                for (int i = 1; i <= noOfDays; i++) {
                    //First check if there is any order existing for the day
                    LocalDate dateOfMonth = LocalDate.of(localDate.getYear(), localDate.getMonth(), i);
                    Instant instant = dateOfMonth.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
                    Date orderDate = Date.from(instant);
                    DailyOrder dailyOrder = dailyOrderRepository.findByUserIdAndOrderDate(user.getId(), orderDate);
                    if (dailyOrder == null && user.getOrderStartDate().compareTo(orderDate) <= 0) {
                        dailyOrder = new DailyOrder();
                        dailyOrder.setUser(user);
                        dailyOrder.setCmOrder(user.getDailyCmOrder());
                        dailyOrder.setBmOrder(user.getDailyBmOrder());
                        dailyOrder.setOrderDate(orderDate);
                        dailyOrderRepository.save(dailyOrder);
                    }
                }
            }
        }
    }
}
