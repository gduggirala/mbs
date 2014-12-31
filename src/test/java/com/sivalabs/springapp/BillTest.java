package com.sivalabs.springapp;

import com.sivalabs.springapp.config.AppConfig;
import com.sivalabs.springapp.entities.Bill;
import com.sivalabs.springapp.entities.DailyOrder;
import com.sivalabs.springapp.entities.User;
import com.sivalabs.springapp.repositories.BillRepository;
import com.sivalabs.springapp.repositories.DailyOrderRepository;
import com.sivalabs.springapp.services.BillService;
import com.sivalabs.springapp.services.DailyOrderService;
import com.sivalabs.springapp.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * User: giridhad
 * Date: 12/20/14
 * Time: 1:30 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class BillTest {
    @Autowired
    private UserService userService;
    @Autowired
    private DailyOrderService dailyOrderService;
    @Autowired
    private BillService billService;
    @Autowired
    private BillRepository billRepository;
    @Autowired
    private DailyOrderRepository dailyOrderRepository;

    @Test
    public void testGenerateUserBill() {
        LocalDate localDate = LocalDate.now();
        List<Bill> billsList = billService.generateCustomerBills(localDate.getMonth(), localDate.getYear());
        Assert.notEmpty(billsList);
    }

    @Test
    public void testRecalculateBill() {
        List<Bill> billList = billService.findAllBills();
        if (billList != null && billList.size() > 0) {
            Bill bill = billList.get(0);
            double  previousTotal = bill.getTotalAmount();
            double previousTotalBmQuantity = bill.getTotalBmQty();
            double previousTotalCmQuantity = bill.getTotalCmQty();
            Date dateNotBefore = bill.getFromDate();
            Date dateNotAfter = bill.getToDate();
            User user = userService.findUserById(bill.getUser().getId());
            double cmPrice = user.getCmPrice();
            double bmPrice = user.getBmPrice();
            if (cmPrice == 0 || bmPrice == 0) {
                throw new RuntimeException("CM Price or BM price for the customer must be set");
            }
            List<DailyOrder> dailyOrders = dailyOrderService.findByUserIdAndOrderDateBetween(user.getId(), dateNotBefore, dateNotAfter);
            for(DailyOrder dailyOrder:dailyOrders){
                dailyOrder.setCmOrder(dailyOrder.getCmOrder()+0.5);
            }
            dailyOrderRepository.save(dailyOrders);
            Bill bill2 = billService.recalculateUserBill(bill);
            Assert.isTrue(previousTotal<bill2.getTotalAmount(),"As the quantity is increased the recalculated total should be greater than previous total");
            Assert.isTrue(previousTotalCmQuantity<bill2.getTotalCmQty(),"As the quantity is increased the recalculated total should be greater than previous total CM");
        }
    }

    @Test
    public void testLocalDate() {
        LocalDate localDate = LocalDate.of(2015, 1, 1);
        System.out.println("Local date is " + localDate);
        System.out.println("Local date's month -1 is " + localDate.minusMonths(1));
    }
}
