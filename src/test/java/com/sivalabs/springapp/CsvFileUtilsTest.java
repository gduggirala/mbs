package com.sivalabs.springapp;

import com.sivalabs.springapp.config.AppConfig;
import com.sivalabs.springapp.entities.User;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * User: duggirag
 * Date: 1/28/15
 * Time: 10:58 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class CsvFileUtilsTest {
    @Autowired
    CsvFileUtils csvFileUtils;

    @Test
    //@Ignore
    public void testImportUsersFromCsv() throws Exception {
        List<User> userList = csvFileUtils.importUsersFromCsv("C:\\Users\\duggirag\\Downloads\\CustomerInfo.csv");
    }
}
