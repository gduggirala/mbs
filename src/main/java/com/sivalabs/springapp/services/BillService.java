package com.sivalabs.springapp.services;

import com.sivalabs.springapp.entities.Bill;
import com.sivalabs.springapp.entities.DailyOrder;
import com.sivalabs.springapp.entities.User;
import com.sivalabs.springapp.repositories.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * User: giridhad
 * Date: 12/20/14
 * Time: 7:32 AM
 */
@Service
@Transactional
public class BillService {
    @Autowired
    private UserService userService;
    @Autowired
    private DailyOrderService dailyOrderService;
    @Autowired
    private BillRepository billRepository;

    public List<Bill> findAllBills() {
        return billRepository.findAll();
    }

    public List<Bill> findByUserId(Long userId) {
        List<Bill> bills = billRepository.findByUserId(userId);
        return bills;
    }

    public Bill findById(Long billId) {
        return billRepository.findById(billId);
    }

    /**
     * This method is used for reporting purpose, we can use
     * the given date is used to derive the start day (which will 1) and end date
     * (Which can be 30, 31, 28 or 29)
     *
     * @param month
     * @param year
     * @return
     */
    public List<Bill> findByMonth(Month month, int year) {
        LocalDate localDate = LocalDate.of(year, month, 1);
        int noOfDays = localDate.getDayOfMonth();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, localDate.getMonth().getValue() - 1);
        calendar.set(Calendar.YEAR, localDate.getYear());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date dateNotBefore = calendar.getTime();

        calendar.set(Calendar.MONTH, localDate.getMonth().getValue() - 1);
        calendar.set(Calendar.YEAR, localDate.getYear());
        calendar.set(Calendar.DAY_OF_MONTH, noOfDays);
        Date dateNotAfter = calendar.getTime();

        List<Bill> billsOfMonth = billRepository.findByFromDateAndToDate(dateNotBefore, dateNotAfter);
        return billsOfMonth;
    }

    /**
     * Front end user will click on "Generate bill" which will trigger this method
     * When the user click's on the button it is assumed that he is requesting for previous month bill
     *
     * @param month
     * @param year  result of localDate.getYear()
     * @return
     */
    public List<Bill> generateCustomerBills(Month month, int year) {
        List<User> users = userService.findByIsActiveTrue();
        List<Bill> usersBill = new ArrayList<>();
        for (User user : users) {
            Bill bill = generateUserBill(user, month, year);
            if (bill != null) {
                usersBill.add(bill);
            }
        }
        return usersBill;
    }

    public Bill recalculateUserBill(Long billId) {
        Bill bill = billRepository.findOne(billId);
        return recalculateUserBill(bill);
    }

    public Bill recalculateUserBill(Bill bill) {
        Date dateNotBefore = bill.getFromDate();
        Date dateNotAfter = bill.getToDate();
        User user = userService.findUserById(bill.getUser().getId());
        double cmPrice = user.getCmPrice();
        double bmPrice = user.getBmPrice();
        if (cmPrice == 0 || bmPrice == 0) {
            throw new RuntimeException("CM Price or BM price for the customer must be set");
        }
        List<DailyOrder> dailyOrders = dailyOrderService.findByUserIdAndOrderDateBetween(user.getId(), dateNotBefore, dateNotAfter);
        double totalCmLiters = 0, totalBmLiters = 0;
        for (DailyOrder dailyOrder : dailyOrders) {
            totalBmLiters = totalBmLiters + dailyOrder.getBmOrder();
            totalCmLiters = totalCmLiters + dailyOrder.getCmOrder();
        }
        double totalCmCost = totalCmLiters * cmPrice;
        double totalBmCost = totalBmLiters * bmPrice;
        double billTotal = totalBmCost + totalCmCost;
        double previousMonthsBalance = bill.getPreviousMonthsBalanceAmount();

        double grandTotal = (billTotal + previousMonthsBalance + bill.getOtherCharges()) - bill.getDiscount();

        bill.setGenerationDate(Calendar.getInstance().getTime());
        bill.setTotalAmount(grandTotal);
        bill.setTotalBmQty(totalBmLiters);
        bill.setTotalBmPrice(totalBmCost);
        bill.setTotalCmQty(totalCmLiters);
        bill.setTotalCmPrice(totalCmCost);
        bill.setBmPerQuantityPrice(user.getBmPrice());
        bill.setCmPerQuantityPrice(user.getCmPrice());
        if (bill.isClosed()) {
            bill.setBalanceAmount(bill.getTotalAmount() - bill.getPaidAmount());
        }
        billRepository.save(bill);
        return bill;
    }

    public Bill generateUserBill(User user, Month month, int year) {
        Calendar calendar = Calendar.getInstance();
        final double cmPrice = user.getCmPrice();
        final double bmPrice = user.getBmPrice();

        if (bmPrice == 0 || cmPrice == 0) {
            throw new RuntimeException("CM Price or BM price for the customer must be set");
        }

        LocalDate localDate = LocalDate.of(year, month, LocalDate.now().getDayOfMonth());

        int noOfDays = localDate.lengthOfMonth();
        calendar.set(Calendar.MONTH, localDate.getMonth().getValue() - 1);
        calendar.set(Calendar.YEAR, localDate.getYear());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date dateNotBefore = calendar.getTime();

        calendar.set(Calendar.MONTH, localDate.getMonth().getValue() - 1);
        calendar.set(Calendar.YEAR, localDate.getYear());
        calendar.set(Calendar.DAY_OF_MONTH, noOfDays);
        Date dateNotAfter = calendar.getTime();

        Bill bill = billRepository.findByUserIdAndFromDateAndToDate(user.getId(), dateNotBefore, dateNotAfter);
        if (bill != null) {
            return bill;
        }

        List<DailyOrder> dailyOrders = dailyOrderService.findByUserIdAndOrderDateBetween(user.getId(), dateNotBefore, dateNotAfter);

        double totalCmLiters = 0, totalBmLiters = 0;
        for (DailyOrder dailyOrder : dailyOrders) {
            totalBmLiters = totalBmLiters + dailyOrder.getBmOrder();
            totalCmLiters = totalCmLiters + dailyOrder.getCmOrder();
        }
        double totalCmCost = totalCmLiters * cmPrice;
        double totalBmCost = totalBmLiters * bmPrice;
        double billTotal = totalBmCost + totalCmCost;

        double previousMonthsBalance = getBalanceOfBill(user, localDate.minusMonths(1));

        double grandTotal = billTotal + previousMonthsBalance;

        bill = new Bill();
        bill.setUser(user);
        bill.setFromDate(dateNotBefore);
        bill.setToDate(dateNotAfter);
        bill.setGenerationDate(Calendar.getInstance().getTime());
        bill.setTotalAmount(grandTotal);
        bill.setTotalBmQty(totalBmLiters);
        bill.setTotalBmPrice(totalBmCost);
        bill.setTotalCmQty(totalCmLiters);
        bill.setTotalCmPrice(totalCmCost);
        bill.setBmPerQuantityPrice(user.getBmPrice());
        bill.setCmPerQuantityPrice(user.getCmPrice());
        bill.setPreviousMonthsBalanceAmount(previousMonthsBalance);
        bill.setComment((previousMonthsBalance < 0 || previousMonthsBalance > 0) ? "Other Charges: Previous Month balance " + previousMonthsBalance : "Other Charges: None");
        billRepository.save(bill);
        return bill;
    }

    /**
     * @param user
     * @param localDate it is the previous months last date
     *                  For example, 2007-03-31 minus one month would result in the invalid date 2007-02-31.
     *                  Instead of returning an invalid result, the last valid day of the month, 2007-02-28, is selected instead.
     * @return
     */
    public double getBalanceOfBill(User user, LocalDate localDate) {
        Bill bill = getBillForTheMonth(user, localDate);
        if (bill != null) {
            return bill.getBalanceAmount();
        } else {
            return 0;
        }
    }

    /**
     * @param user
     * @param localDate it is the previous months last date
     *                  For example, 2007-03-31 minus one month would result in the invalid date 2007-02-31.
     *                  Instead of returning an invalid result, the last valid day of the month, 2007-02-28, is selected instead.
     * @return
     */
    public Bill getBillForTheMonth(User user, LocalDate localDate) {
        Calendar calendar = Calendar.getInstance();
        int noOfDays = localDate.lengthOfMonth();
        calendar.set(Calendar.MONTH, localDate.getMonth().getValue() - 1);
        calendar.set(Calendar.YEAR, localDate.getYear());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date dateNotBefore = calendar.getTime();

        calendar.set(Calendar.MONTH, localDate.getMonth().getValue() - 1);
        calendar.set(Calendar.YEAR, localDate.getYear());
        calendar.set(Calendar.DAY_OF_MONTH, noOfDays);
        Date dateNotAfter = calendar.getTime();

        return billRepository.findByUserIdAndFromDateAndToDate(user.getId(), dateNotBefore, dateNotAfter);
    }

    public Bill update(Bill bill) {
        //When the bill is being updated we have to recalculate the total and everything again..
        billRepository.save(bill);
        return recalculateUserBill(bill);
    }
}
