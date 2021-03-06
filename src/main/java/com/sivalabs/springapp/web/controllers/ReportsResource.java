package com.sivalabs.springapp.web.controllers;

import ch.lambdaj.Lambda;
import com.sivalabs.springapp.DateUtils;
import com.sivalabs.springapp.entities.Bill;
import com.sivalabs.springapp.reports.pojo.DailyOrderGround;
import com.sivalabs.springapp.reports.pojo.DailyOrderReport;
import com.sivalabs.springapp.reports.service.ReportsGenerator;
import com.sivalabs.springapp.services.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

/**
 * User: gduggirala
 * Date: 4/1/15
 * Time: 1:07 PM
 */
@RestController
@RequestMapping("/rest/report/")
public class ReportsResource {
    @Autowired
    private ReportsGenerator reportsGenerator;
    @Autowired
    private BillService billService;

    @RequestMapping(value="/dailyOrderReport", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generateReportByDate() throws SQLException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = simpleDateFormat.format(new Date());
        List<DailyOrderReport> dailyOrderReportList = reportsGenerator.generateDailyOrderReport(new Date());
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("success",Boolean.TRUE);
        userMap.put("total",dailyOrderReportList.size());
        userMap.put("dailyOrderReport",dailyOrderReportList);
        userMap.put("grandTotalCmOrder",sum(dailyOrderReportList, on(DailyOrderReport.class).getTotalCmOrder()));
        userMap.put("grandTotalBmOrder",sumFrom(dailyOrderReportList).getTotalBmOrder());
        userMap.put("totalCmRevenue",sumFrom(dailyOrderReportList).getCmRevenue());
        userMap.put("totalBmRevenue",sumFrom(dailyOrderReportList).getBmRevenue());
        userMap.put("totalMilkRevenue",(sumFrom(dailyOrderReportList).getBmRevenue() + sumFrom(dailyOrderReportList).getCmRevenue()));
        userMap.put("forDate",formattedDate);
        return new ResponseEntity<>(userMap, HttpStatus.OK);
    }

    @RequestMapping(value="/monthlyBills", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generatePaidToUnpaidBills() throws SQLException {
        LocalDate localDate = LocalDate.now();
        List<Bill> billList = billService.findByMonth(localDate.minusMonths(1).getMonth(), localDate.minusMonths(1).getYear());
        List<Bill> paidBills = Lambda.filter(having(on(Bill.class).getPaidAmount(),greaterThan(0d)),billList);
        List<Bill> unpaidBills = Lambda.filter(having(on(Bill.class).isPaid(),is(Boolean.FALSE)),billList);
        Map<String, Object> userMap = new HashMap<>();
        Double unpaidAmount =(double) 0;
        Double paidAmount =(double) 0;
        Double totalRevenue = (double) 0;
        if(!unpaidBills.isEmpty()){
            unpaidAmount = sumFrom(unpaidBills).getPayableAmount();
        }
        if(!paidBills.isEmpty()){
            paidAmount = sumFrom(paidBills).getPaidAmount();
        }
        if (!billList.isEmpty()){
            totalRevenue = sumFrom(billList).getBillableAmount();
        }
        userMap.put("success",Boolean.TRUE);
        userMap.put("billList",billList);
        userMap.put("paidBills",paidBills);
        userMap.put("unpaidBills",unpaidBills);
        userMap.put("paidBillsCount",paidBills.size());
        userMap.put("unpaidBillsCount",unpaidBills.size());
        userMap.put("unpaidAmount",unpaidAmount);
        userMap.put("amountYetToBePaid",totalRevenue-paidAmount);
        userMap.put("paidAmount",paidAmount);
        userMap.put("totalRevenue",totalRevenue);
        userMap.put("forMonth",localDate.minusMonths(1).getMonth().getDisplayName(TextStyle.FULL, Locale.US));
        return new ResponseEntity<>(userMap, HttpStatus.OK);
    }

    @RequestMapping(value="/dailyOrderGroundReport", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generateDailyOrderGroundReport() throws SQLException {
        LocalDate localDate = LocalDate.now();
        List<DailyOrderGround> dailyOrderGroundList = reportsGenerator.generateDailyOrderGroundReport(DateUtils.asDate(localDate));
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("success",Boolean.TRUE);
        userMap.put("total",dailyOrderGroundList.size());
        userMap.put("dailyOrderGrounds",dailyOrderGroundList);
        return new ResponseEntity<Map<String, Object>>(userMap, HttpStatus.OK);
    }

    @RequestMapping(value="/listBills", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> listBills(@RequestParam(required = false, value="month")String fullMonth) throws SQLException {
        LocalDate localDate = null;
        LocalDate tmp = LocalDate.now();
        if (fullMonth == null || fullMonth.isEmpty()){
            localDate = LocalDate.now();
        }else {
            LocalDate tempDate = LocalDate.of(tmp.getYear(),Month.valueOf(fullMonth.toUpperCase()),1);
            localDate = tempDate.plusMonths(1);
        }
        LocalDate previousMonth = localDate.minusMonths(1);
        LocalDate previousPreviousMonth = localDate.minusMonths(2);
        List<Bill> billListReports = reportsGenerator.generateBillReport(previousMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.US), previousPreviousMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.US));
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("success",Boolean.TRUE);
        userMap.put("total",billListReports.size());
        userMap.put("billListReports",billListReports);
        return new ResponseEntity<Map<String, Object>>(userMap, HttpStatus.OK);
    }

}
