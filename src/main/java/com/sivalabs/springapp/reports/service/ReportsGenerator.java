package com.sivalabs.springapp.reports.service;

import com.sivalabs.springapp.DateUtils;
import com.sivalabs.springapp.reports.pojo.BillListReport;
import com.sivalabs.springapp.reports.pojo.DailyOrderGround;
import com.sivalabs.springapp.reports.pojo.DailyOrderReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: gduggirala
 * Date: 4/1/15
 * Time: 12:53 PM
 */
@Service
public class ReportsGenerator {

    @Autowired
    private DataSource dataSource;


    public List<DailyOrderReport> generateDailyOrderReport(Date date) throws SQLException {
        return generateDailyOrderReport(DateUtils.getAppFormattedDate(date));
    }

    public List<DailyOrderReport> generateDailyOrderReport(String formattedDate) throws SQLException {

        String sql = "SELECT sum(d0.bmOrder) AS 'totalBmOrder', (sum(d0.bmOrder) * u.bmPrice) as 'bmRevenue',  sum(d0.cmOrder) AS 'totalCmOrder', (sum(d0.cmOrder) * u.cmPrice) as 'cmRevenue', u.sector AS 'sector' " +
                " FROM DAILY_ORDER d0 " +
                " INNER JOIN USERS u ON d0.user_Id=u.id " +
                " WHERE orderDate = ? and u.isActive=true " +
                " GROUP BY u.sector";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<DailyOrderReport> dailyOrderReportList = jdbcTemplate.query(sql, new Object[]{formattedDate}, new BeanPropertyRowMapper(DailyOrderReport.class));
        return dailyOrderReportList;
    }

    public List<DailyOrderGround> generateDailyOrderGroundReport(Date date) throws SQLException{
        return generateDailyOrderGroundReport(DateUtils.getAppFormattedDate(date));
    }

    public List<DailyOrderReport> generateRevenueTrend(){
        String sql = "SELECT (SUM(d0.bmOrder) * u.bmPrice) AS 'bmRevenue', (SUM(d0.cmOrder) * u.cmPrice) AS 'cmRevenue',  " +
                " DATE_FORMAT(d0.orderDate, '%M') AS 'orderMonth' " +
                "FROM DAILY_ORDER d0 " +
                "INNER JOIN USERS u ON d0.user_Id=u.id " +
                "WHERE u.isActive= TRUE " +
                "GROUP BY YEAR(d0.orderDate), MONTH(d0.orderDate)";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<DailyOrderReport> dailyOrderReportList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(DailyOrderReport.class));
        return dailyOrderReportList;
    }

    public List<DailyOrderGround> generateDailyOrderGroundReport(String formattedDate) throws SQLException{
        String sql ="SELECT d0.id as 'id', d0.bmOrder AS 'bmOrder', d0.cmOrder AS 'cmOrder', u.sector AS 'sector', u.name AS 'name', u.phone AS 'phone', d0.orderDate AS 'orderDate', u.givenSerialNumber AS 'givenSerialNumber', u.address1 as 'address1' " +
                "FROM DAILY_ORDER d0 " +
                "INNER JOIN USERS u ON d0.user_Id=u.id " +
                "WHERE orderDate = ? AND u.isActive= TRUE ORDER BY `sector`";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<DailyOrderGround> dailyOrderGroundList = jdbcTemplate.query(sql, new Object[]{formattedDate}, new BeanPropertyRowMapper(DailyOrderGround.class));
        return dailyOrderGroundList;
    }

    public List<BillListReport> generateBillReport(String previousMonth, String previousPreviousMonth) throws SQLException{
        List<String> months = new ArrayList<>(2);
        months.add(previousMonth);
        months.add(previousPreviousMonth);
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("months", months);
        String sql =String.format("SELECT b0.*, DATE_FORMAT(b0.toDate, '%%M') AS 'month', u.id as 'customerId', u.name, u.sector, u.address1, u.givenSerialNumber, u.phone, u.dailybmOrder, u.dailyCmOrder " +
                "FROM BILL b0 " +
                "INNER JOIN USERS u ON b0.USER_ID = u.id where DATE_FORMAT(b0.toDate, '%%M') in ('%s', '%s')",previousMonth, previousPreviousMonth);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<BillListReport> billListReports = jdbcTemplate.query(sql, new BeanPropertyRowMapper(BillListReport.class));
        return billListReports;
    }

    public List<BillListReport> generateBillReport(String month) throws SQLException{
        List<String> months = new ArrayList<>(2);
        months.add(month);
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("months", months);
        String sql =String.format("SELECT b0.*, DATE_FORMAT(b0.toDate, '%%M') AS 'month', u.id as 'customerId', u.name, u.sector, u.address1, u.givenSerialNumber, u.phone, u.dailybmOrder, u.dailyCmOrder " +
                "FROM BILL b0 " +
                "INNER JOIN USERS u ON b0.USER_ID = u.id where DATE_FORMAT(b0.toDate, '%%M') in ('%s')",month);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<BillListReport> billListReports = jdbcTemplate.query(sql, new BeanPropertyRowMapper(BillListReport.class));
        return billListReports;
    }
}
