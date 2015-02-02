package com.sivalabs.springapp.web.controllers;

import ch.lambdaj.Lambda;
import com.sivalabs.springapp.DateUtils;
import com.sivalabs.springapp.entities.Bill;
import com.sivalabs.springapp.reports.pojo.BillListReport;
import com.sivalabs.springapp.reports.pojo.DailyOrderGround;
import com.sivalabs.springapp.reports.pojo.DailyOrderReport;
import com.sivalabs.springapp.reports.service.ReportsGenerator;
import com.sivalabs.springapp.services.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

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
        List<Bill> unpaidBills = Lambda.filter(having(on(Bill.class).getPaidAmount(),lessThan(1d)),billList);
        Map<String, Object> userMap = new HashMap<>();
        Double unpaidAmount =(double) 0;
        Double paidAmount =(double) 0;
        if(!unpaidBills.isEmpty()){
            unpaidAmount = sumFrom(unpaidBills).getPayableAmount();
        }
        if(!paidBills.isEmpty()){
            paidAmount = sumFrom(paidBills).getPaidAmount();
        }
        userMap.put("success",Boolean.TRUE);
        userMap.put("billList",billList);
        userMap.put("paidBills",paidBills);
        userMap.put("unpaidBills",unpaidBills);
        userMap.put("paidBillsCount",paidBills.size());
        userMap.put("unpaidBillsCount",unpaidBills.size());
        userMap.put("unpaidAmount",unpaidAmount);
        userMap.put("amountYetToBePaid",unpaidAmount-paidAmount);
        userMap.put("paidAmount",paidAmount);
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
    public ResponseEntity<Map<String, Object>> listBills() throws SQLException {
        LocalDate localDate = LocalDate.now();
        LocalDate previousMonth = localDate.minusMonths(1);
        LocalDate previousPreviousMonth = localDate.minusMonths(2);
        List<BillListReport> billListReports = reportsGenerator.generateBillReport(previousMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.US), previousPreviousMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.US));
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("success",Boolean.TRUE);
        userMap.put("total",billListReports.size());
        userMap.put("billListReports",billListReports);
        return new ResponseEntity<Map<String, Object>>(userMap, HttpStatus.OK);
    }

}
