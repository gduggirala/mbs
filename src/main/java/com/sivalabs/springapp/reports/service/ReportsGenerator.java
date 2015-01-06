package com.sivalabs.springapp.reports.service;

import com.sivalabs.springapp.reports.pojo.DailyOrderReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = simpleDateFormat.format(date);
        return generateDailyOrderReport(formattedDate);
    }

    public List<DailyOrderReport> generateDailyOrderReport(String formattedDate) throws SQLException {

        String sql = "SELECT sum(d0.bmOrder) AS 'totalBmOrder', (sum(d0.bmOrder) * u.bmPrice) as 'bmRevenue',  sum(d0.cmOrder) AS 'totalCmOrder', (sum(d0.cmOrder) * u.cmPrice) as 'cmRevenue', u.sector AS 'sector'\n" +
                " FROM daily_order d0\n" +
                " INNER JOIN users u ON d0.user_Id=u.id\n" +
                " WHERE orderDate = ? and u.isActive=true\n" +
                " GROUP BY u.sector";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<DailyOrderReport> dailyOrderReportList = jdbcTemplate.query(sql, new Object[]{formattedDate}, new BeanPropertyRowMapper(DailyOrderReport.class));
        return dailyOrderReportList;
    }
}
