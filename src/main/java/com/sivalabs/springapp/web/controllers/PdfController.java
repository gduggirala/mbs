package com.sivalabs.springapp.web.controllers;

import com.sivalabs.springapp.DateUtils;
import com.sivalabs.springapp.entities.DailyOrder;
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
import java.util.Date;
import java.util.List;

/**
 * User: duggirag
 * Date: 1/8/15
 * Time: 4:27 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class PdfController {

    @Autowired
    DailyOrderService dailyOrderService;
    @Autowired
    ReportsGenerator reportsGenerator;

    @RequestMapping(value = "/generate/dailyOrder", method = RequestMethod.GET)
    ModelAndView generatePdf(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        Date dateOfConsideration = new Date();
        String formattedDateForReport= DateUtils.getFormattedDateForReport(dateOfConsideration);
        List<DailyOrderGround> dailyOrderGroundList = reportsGenerator.generateDailyOrderGroundReport(dateOfConsideration);
        ModelAndView modelAndView = new ModelAndView("pdfView", "dailyOrderGroundList", dailyOrderGroundList);
        modelAndView.addObject("formattedDateForReport",formattedDateForReport);
        return modelAndView;
    }
}