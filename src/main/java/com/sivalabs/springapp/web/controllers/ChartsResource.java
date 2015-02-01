package com.sivalabs.springapp.web.controllers;

import com.sivalabs.springapp.DateUtils;
import com.sivalabs.springapp.reports.pojo.DailyOrderGround;
import com.sivalabs.springapp.reports.pojo.DailyOrderReport;
import com.sivalabs.springapp.reports.service.ReportsGenerator;
import com.sivalabs.springapp.services.BillService;
import com.sivalabs.springapp.services.DailyOrderService;
import com.sivalabs.springapp.services.UserService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.TemporalField;
import java.util.*;

/**
 * User: duggirag
 * Date: 1/29/15
 * Time: 10:54 AM
 */
@RestController
@RequestMapping("/rest/charts/")
public class ChartsResource {
    @Autowired
    DailyOrderService dailyOrderService;
    @Autowired
    BillService billService;
    @Autowired
    UserService userService;
    @Autowired
    ReportsGenerator reportsGenerator;

    @RequestMapping(value = "/dailyOrderReportChart", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public void generateDailyOrderGroundReportChart(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = simpleDateFormat.format(new Date());
        List<DailyOrderReport> dailyOrderReportList = reportsGenerator.generateDailyOrderReport(new Date());
        Map<String, Object> userMap = new HashMap<>();
        int height = 500;
        int width = new Integer(request.getParameter("width"));
        JFreeChart barChart = ChartFactory.createBarChart3D(
                "Daily order for " + formattedDate,
                "Sector",
                "Liters",
                createDataset(dailyOrderReportList),
                PlotOrientation.VERTICAL,
                true, true, true);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ChartUtilities.writeChartAsJPEG(bos, barChart, width, height);
        response.setContentType("image/png");
        OutputStream out = new BufferedOutputStream(response.getOutputStream());
        out.write(bos.toByteArray());
        out.flush();
        out.close();
    }

    @RequestMapping(value = "/dailyOrderRevenueTrend", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public void generateRevenueTrendForTheMonth(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        List<DailyOrderReport> dailyOrderReportList = reportsGenerator.generateRevenueTrend();
        Map<String, Object> userMap = new HashMap<>();
        int height = 500;
        int width = new Integer(request.getParameter("width"));;
        JFreeChart barChart = ChartFactory.createBarChart3D(
                "Revenue Trend ",
                "Month",
                "Rs.",
                createRevenueDataset(dailyOrderReportList),
                PlotOrientation.VERTICAL,
                true, true, true);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ChartUtilities.writeChartAsJPEG(bos, barChart, width, height);
        response.setContentType("image/png");
        OutputStream out = new BufferedOutputStream(response.getOutputStream());
        out.write(bos.toByteArray());
        out.flush();
        out.close();
    }

    private CategoryDataset createDataset(List<DailyOrderReport> dailyOrderReportList) {
        DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();
        for (DailyOrderReport dailyOrderReport : dailyOrderReportList) {
            categoryDataset.addValue(dailyOrderReport.getTotalBmOrder(), "BM", dailyOrderReport.getSector());
            categoryDataset.addValue(dailyOrderReport.getTotalCmOrder(), "CM", dailyOrderReport.getSector());
        }

        return categoryDataset;  //To change body of created methods use File | Settings | File Templates.
    }

    private CategoryDataset createRevenueDataset(List<DailyOrderReport> dailyOrderReportList) {
        DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();
        for (DailyOrderReport dailyOrderReport : dailyOrderReportList) {
            categoryDataset.addValue(dailyOrderReport.getBmRevenue(), "BM", dailyOrderReport.getOrderMonth());
            categoryDataset.addValue(dailyOrderReport.getCmRevenue(), "CM", dailyOrderReport.getOrderMonth());
        }

        return categoryDataset;  //To change body of created methods use File | Settings | File Templates.
    }
}
