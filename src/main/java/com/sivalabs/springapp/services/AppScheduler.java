package com.sivalabs.springapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.Month;

/**
 * User: giridhad
 * Date: 12/20/14
 * Time: 11:33 AM
 */
@EnableScheduling
public class AppScheduler {
    @Autowired
    BillService billService;
    @Autowired
    DailyOrderService dailyOrderService;

    /**
     * This will fire at 11:15 PM on the last day of every month
     */
    @Scheduled(cron = "0 15 23 L * ?")
    public void generateMonthlyBill(){
        LocalDate localDate = LocalDate.now();
        billService.generateCustomerBills(localDate.getMonth(), localDate.getYear());
    }

    @Scheduled(cron = "30 2 * * * ?")
    public void generateDailyOrder() throws ParseException {
        LocalDate localDate = LocalDate.now();
        dailyOrderService.createDailyOrdersForAllActiveUsers();
    }
}
