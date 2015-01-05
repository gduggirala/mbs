package com.sivalabs.springapp.reports.service;

import com.sivalabs.springapp.config.AppConfig;
import com.sivalabs.springapp.config.PersistenceConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 4/1/15
 * Time: 2:12 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class, PersistenceConfig.class})
public class ReportsGeneratorTest {
    @Autowired
    ReportsGenerator reportsGenerator;

    @Test
    public void testGenerateDailyOrderReport() throws Exception {
            reportsGenerator.generateDailyOrderReport(new Date());
    }
}
