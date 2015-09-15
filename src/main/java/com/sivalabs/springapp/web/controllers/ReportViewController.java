package com.sivalabs.springapp.web.controllers;

import com.sivalabs.springapp.DateUtils;
import com.sivalabs.springapp.entities.Bill;
import com.sivalabs.springapp.reports.pojo.DailyOrderGround;
import com.sivalabs.springapp.reports.service.ReportsGenerator;
import com.sivalabs.springapp.services.DailyOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * User: duggirag
 * Date: 1/8/15
 * Time: 4:27 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class ReportViewController {

    @Autowired
    DailyOrderService dailyOrderService;
    @Autowired
    ReportsGenerator reportsGenerator;

    @RequestMapping(value = "/generate/dailyOrder.pdf", method = RequestMethod.GET)
    ModelAndView generatePdf(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        Date dateOfConsideration = new Date();
        String formattedDateForReport= DateUtils.getFormattedDateForReport(dateOfConsideration);
        List<DailyOrderGround> dailyOrderGroundList = reportsGenerator.generateDailyOrderGroundReport(dateOfConsideration);
        ModelAndView modelAndView = new ModelAndView("pdfView", "dailyOrderGroundList", dailyOrderGroundList);
        modelAndView.addObject("formattedDateForReport",formattedDateForReport);
        return modelAndView;
    }

    @RequestMapping(value = "/generate/billsPdf.pdf", method = RequestMethod.GET)
    ModelAndView generateBills(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        LocalDate localDate = LocalDate.now();
        LocalDate previousMonth = localDate.minusMonths(1);
        List<Bill> billListReports = reportsGenerator.generateBillReport(previousMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.US));
        ModelAndView modelAndView = new ModelAndView("billsPdfView", "billListReports", billListReports);
        return modelAndView;
    }

    @RequestMapping(value = "/generate/billsExcel.xls", method = RequestMethod.GET)
    ModelAndView generateBillsXls(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        LocalDate localDate = LocalDate.now();
        LocalDate previousMonth = localDate.minusMonths(1);
        String billingMonth = previousMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        List<Bill> billListReports = reportsGenerator.generateBillReport(previousMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.US));
        ModelAndView modelAndView = new ModelAndView("billsXlsView", "billListReports", billListReports);
        modelAndView.getModelMap().put("billingMonth",billingMonth);
        return modelAndView;
    }

    @RequestMapping(value = "/generate/dailyOrder.xls", method = RequestMethod.GET)
    ModelAndView generateExcel(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        Date dateOfConsideration = new Date();
        String formattedDateForReport= DateUtils.getFormattedDateForReport(dateOfConsideration);
        List<DailyOrderGround> dailyOrderGroundList = reportsGenerator.generateDailyOrderGroundReport(dateOfConsideration);
        ModelAndView modelAndView = new ModelAndView("xlsView", "dailyOrderGroundList", dailyOrderGroundList);
        modelAndView.addObject("formattedDateForReport",formattedDateForReport);
        return modelAndView;

    }

}
