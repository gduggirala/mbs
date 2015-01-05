package com.sivalabs.springapp.web.controllers;

import com.sivalabs.springapp.reports.pojo.DailyOrderReport;
import com.sivalabs.springapp.reports.service.ReportsGenerator;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sum;
import static ch.lambdaj.Lambda.sumFrom;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 4/1/15
 * Time: 1:07 PM
 */
@RestController
@RequestMapping("/rest/report/")
public class ReportsResource {
    @Autowired
    private ReportsGenerator reportsGenerator;

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
        userMap.put("forDate",formattedDate);
        return new ResponseEntity<Map<String, Object>>(userMap, HttpStatus.OK);
    }
}
