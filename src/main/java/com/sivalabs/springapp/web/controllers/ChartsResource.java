package com.sivalabs.springapp.web.controllers;

import com.sivalabs.springapp.reports.pojo.DailyOrderReport;
import com.sivalabs.springapp.reports.service.ReportsGenerator;
import com.sivalabs.springapp.services.BillService;
import com.sivalabs.springapp.services.DailyOrderService;
import com.sivalabs.springapp.services.UserService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
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
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

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
        int width = Double.valueOf(request.getParameter("width")).intValue();
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

    @RequestMapping(value = "/dailyRevenueTrend", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public void generateRevenueTrendForTheDay(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        LocalDate localDate = LocalDate.now();
        List<DailyOrderReport> dailyOrderReportList = reportsGenerator.generateDailyRevenueTrend(localDate.getMonth());
        int height = 500;
        int width = Double.valueOf(request.getParameter("width")).intValue();
        JFreeChart barChart = ChartFactory.createBarChart3D(
                "Daily Revenue Trend ("+localDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.US)+")",
                "Day",
                "\u20B9. (x1000)",
                createDailyRevenueDataset(dailyOrderReportList),
                PlotOrientation.VERTICAL,
                true, true, true);
        final CategoryPlot plot = barChart.getCategoryPlot();
        final CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesItemLabelGenerator(0, new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getCurrencyInstance(new Locale("en", "IN"))));
        renderer.setSeriesItemLabelGenerator(1, new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getCurrencyInstance(new Locale("en", "IN"))));
        renderer.setSeriesItemLabelsVisible(0, true);
        renderer.setSeriesItemLabelsVisible(1, true);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ChartUtilities.writeChartAsJPEG(bos, barChart, width, height);
        response.setContentType("image/png");
        OutputStream out = new BufferedOutputStream(response.getOutputStream());
        out.write(bos.toByteArray());
        out.flush();
        out.close();
    }
    @RequestMapping(value = "/monthlyRevenueTrend", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public void generateRevenueTrendForTheMonth(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        List<DailyOrderReport> dailyOrderReportList = reportsGenerator.generateRevenueTrend();
        int height = 500;
        int width = Double.valueOf(request.getParameter("width")).intValue();
        JFreeChart barChart = ChartFactory.createBarChart3D(
                "Monthly Revenue Trend ",
                "Month",
                "\u20B9.",
                createMonthlyRevenueDataset(dailyOrderReportList),
                PlotOrientation.VERTICAL,
                true, true, true);
        final CategoryPlot plot = barChart.getCategoryPlot();
        final CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesItemLabelGenerator(0, new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getCurrencyInstance(new Locale("en", "IN"))));
        renderer.setSeriesItemLabelGenerator(1, new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getCurrencyInstance(new Locale("en", "IN"))));
        renderer.setSeriesItemLabelsVisible(0, true);
        renderer.setSeriesItemLabelsVisible(1, true);
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

    private CategoryDataset createMonthlyRevenueDataset(List<DailyOrderReport> dailyOrderReportList) {
        DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();
        for (DailyOrderReport dailyOrderReport : dailyOrderReportList) {
            categoryDataset.addValue(dailyOrderReport.getBmRevenue(), "BM", dailyOrderReport.getOrderMonth());
            categoryDataset.addValue(dailyOrderReport.getCmRevenue(), "CM", dailyOrderReport.getOrderMonth());
        }

        return categoryDataset;  //To change body of created methods use File | Settings | File Templates.
    }

    private CategoryDataset createDailyRevenueDataset(List<DailyOrderReport> dailyOrderReportList) {
        DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();
        DateFormat dateFormat = new SimpleDateFormat("dd");
        for (DailyOrderReport dailyOrderReport : dailyOrderReportList) {
            //categoryDataset.addValue(dailyOrderReport.getTotalBmOrder(), "BM", dateFormat.format(dailyOrderReport.getOrderDate()));
            categoryDataset.addValue((dailyOrderReport.getBmRevenue()/1000), "BM", dateFormat.format(dailyOrderReport.getOrderDate()));
            //categoryDataset.addValue(dailyOrderReport.getTotalCmOrder(), "CM", dateFormat.format(dailyOrderReport.getOrderDate()));
            categoryDataset.addValue((dailyOrderReport.getCmRevenue()/1000), "CM", dateFormat.format(dailyOrderReport.getOrderDate()));
        }
        return categoryDataset;  //To change body of created methods use File | Settings | File Templates.
    }
}
