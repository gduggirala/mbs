package com.sivalabs.springapp.services;

import com.sivalabs.springapp.DateUtils;
import com.sivalabs.springapp.entities.DailyOrder;
import com.sivalabs.springapp.entities.User;
import com.sivalabs.springapp.repositories.DailyOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * User: giridhad
 * Date: 12/19/14
 * Time: 3:07 PM
 */
@Service
@Transactional
public class DailyOrderService {
    @Autowired
    private UserService userService;
    @Autowired
    private DailyOrderRepository dailyOrderRepository;

    public DailyOrder findById(Long id) {
        return dailyOrderRepository.findById(id);
    }

    public List<DailyOrder> findAllDailyOrders() {
        return dailyOrderRepository.findAll();
    }

    public DailyOrder createOrUpdateDailyOrder(Double cmQuantity, Double bmQuantity, Date orderDate, Long userId) {
        DailyOrder dailyOrder = dailyOrderRepository.findByUserIdAndOrderDate(userId, orderDate);
        User user = userService.findUserById(userId);
        if (dailyOrder == null) {
            dailyOrder = new DailyOrder();
            dailyOrder.setCmOrder(cmQuantity);
            dailyOrder.setBmOrder(bmQuantity);
            dailyOrder.setOrderDate(orderDate);
            dailyOrder.setUser(user);
            dailyOrderRepository.save(dailyOrder);
        } else {
            dailyOrder.setCmOrder(cmQuantity);
            dailyOrder.setBmOrder(bmQuantity);
            dailyOrderRepository.saveAndFlush(dailyOrder);
        }
        return dailyOrder;
    }

    /**
     * Return all the daily orders irrespective of userId, it will be used for getting the Overview of daily orders.
     *
     * @param orderDate
     * @return
     */
    public List<DailyOrder> findAllDailyOrdersByDate(Date orderDate) {
        return dailyOrderRepository.findByOrderDate(orderDate);
    }

    public List<DailyOrder> findByUserId(Long userId) {
        List<DailyOrder> dailyOrders = dailyOrderRepository.findByUserId(userId);
        return dailyOrders;
    }

    public List<DailyOrder> findByUserIdAndOrderDateBetween(Long userId, Date orderDateNotBefore, Date orderDateNotAfter) {
        List<DailyOrder> dailyOrders = dailyOrderRepository.findByUserIdAndOrderDateBetween(userId, orderDateNotBefore, orderDateNotAfter);
        return dailyOrders;
    }

    public DailyOrder create(DailyOrder dailyOrder) {
        return dailyOrderRepository.save(dailyOrder);
    }

    /**
     * This method will be used by the background jobs to populate a daily order.
     * The following is the condition
     * 1. If there is no Daily Order then refer to user daily Order and fill it in.
     * 2. If there is daily order then don't make any changes just let it be as is.
     * 3. Only deals with current month (Assuming that this will be executed once everyday at mid night).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createDailyOrdersForAllActiveUsers() {
        List<User> users = userService.findByIsActiveTrue();
        for (User user : users) {
            createDailyOrderForUser(user);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void createDailyOrderForUser(User user) {
        LocalDate localDate = LocalDate.now();
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

    public DailyOrder update(DailyOrder dailyOrder) {
        return dailyOrderRepository.save(dailyOrder);
    }

    public void finalizeDailyOrderForUser(User user, Date lastDateOfService) {
        LocalDate serviceLastDate = lastDateOfService.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate serviceMonthEndingDate = LocalDate.of(serviceLastDate.getYear(), serviceLastDate.getMonth(), serviceLastDate.lengthOfMonth());
        List<DailyOrder> dailyOrders = findByUserIdAndOrderDateBetween(user.getId(), DateUtils.asDate(serviceLastDate.plusDays(1)), DateUtils.asDate(serviceMonthEndingDate));
        if (dailyOrders != null && !dailyOrders.isEmpty()) {
            for (DailyOrder dailyOrder : dailyOrders) {
                dailyOrder.setBmOrder((double) 0);
                dailyOrder.setCmOrder((double) 0);
                dailyOrderRepository.saveAndFlush(dailyOrder);
            }
        }
    }
}
