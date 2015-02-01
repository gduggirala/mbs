package com.sivalabs.springapp.services;

import ch.lambdaj.Lambda;
import com.sivalabs.springapp.entities.Bill;
import com.sivalabs.springapp.entities.DailyOrder;
import com.sivalabs.springapp.entities.User;
import com.sivalabs.springapp.repositories.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.ZoneId;
import java.util.*;

import static ch.lambdaj.Lambda.on;

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
        int noOfDays = localDate.lengthOfMonth();

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

    public Bill recalculateUserBillByUser(Long userId) {
        User user = userService.findUserById(userId);
        Set<Bill> billSet = user.getBills();
        //Sorted in ascending order.
        List<Bill> sortedBills = Lambda.sort(billSet, on(Bill.class).getId());
        Bill latestBill = sortedBills.get(sortedBills.size()-1);
        return recalculateUserBill(latestBill);
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
        /*for (DailyOrder dailyOrder : dailyOrders) {
            totalBmLiters = totalBmLiters + dailyOrder.getBmOrder();
            totalCmLiters = totalCmLiters + dailyOrder.getCmOrder();
        }*/
        for(int i=0;i<dailyOrders.size();i++){
            totalBmLiters = totalBmLiters + dailyOrders.get(i).getBmOrder();
            totalCmLiters = totalCmLiters + dailyOrders.get(i).getCmOrder();
        }
        double totalCmCost = totalCmLiters * cmPrice;
        double totalBmCost = totalBmLiters * bmPrice;
        double billTotal = totalBmCost + totalCmCost;
        double previousMonthsBalance = bill.getPreviousMonthsBalanceAmount();

        double grandTotal = (billTotal + previousMonthsBalance + (bill.getOtherCharges()==null?0.0:bill.getOtherCharges())) - (bill.getDiscount()==null?0.0:bill.getDiscount());

        bill.setGenerationDate(Calendar.getInstance().getTime());
        bill.setTotalAmount(grandTotal);
        bill.setTotalBmQty(totalBmLiters);
        bill.setTotalBmPrice(totalBmCost);
        bill.setTotalCmQty(totalCmLiters);
        bill.setTotalCmPrice(totalCmCost);
        bill.setBmPerQuantityPrice(user.getBmPrice());
        bill.setCmPerQuantityPrice(user.getCmPrice());
        if (bill.getPaidAmount() != null) {
            bill.setBalanceAmount(bill.getTotalAmount() - bill.getPaidAmount());
        }
        billRepository.save(bill);
        return bill;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
        bill.setPaidAmount(Double.valueOf(0));
        bill.setOtherCharges(Double.valueOf(0));
        bill.setDiscount(Double.valueOf(0));
        if (bill.getPaidAmount() != null) {
            bill.setBalanceAmount(bill.getTotalAmount() - bill.getPaidAmount());
        }
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

    public void finalizeBillForUser(User user, Date lastDateOfService) {
        dailyOrderService.finalizeDailyOrderForUser(user, lastDateOfService);
    }
}
