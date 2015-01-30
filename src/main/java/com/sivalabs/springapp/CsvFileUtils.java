package com.sivalabs.springapp;

import au.com.bytecode.opencsv.CSVReader;
import com.sivalabs.springapp.entities.User;
import com.sivalabs.springapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * User: duggirag
 * Date: 1/18/15
 * Time: 8:24 AM
 */
@Component
public class CsvFileUtils {

    @Autowired
    UserService userService;

    public List<User> importUsersFromCsv(String csvFilePath) throws IOException {
        List<User> userList = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(csvFilePath));
        CSVReader csvReader = new CSVReader(bufferedReader);

        String[] lineReader = csvReader.readNext();
        int i = 0;
        while (lineReader != null) {
            String[] customerInfo = lineReader;
            if (i != 0 && customerInfo != null && customerInfo.length == 8) {
                User user = new User();
                user.setEmail(i + "@gmail.com");
                user.setGivenSerialNumber(new Integer(customerInfo[0]));
                user.setSector(customerInfo[1]);
                user.setName(customerInfo[2]);
                user.setAddress1(customerInfo[3]);
                user.setDailyBmOrder(new Double(customerInfo[4]));
                user.setDailyCmOrder(new Double(customerInfo[5]));
                user.setBmPrice(new Double(customerInfo[6]));
                user.setCmPrice(new Double(customerInfo[7]));
                user.setActive(Boolean.TRUE);
                user.setPhone("Change Me");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                user.setOrderStartDate(calendar.getTime());
                System.out.println(user);
                userService.create(user);
                userList.add(user);
            } else {
                if (customerInfo == null) {
                    System.out.println("I am null");
                }
                if (customerInfo.length != 8) {
                    System.out.println("Length is not matching " + customerInfo.length);
                }

            }
            i++;
            lineReader = csvReader.readNext();
        }
        System.out.println("Total lines read " + i);
        return userList;
    }
}
