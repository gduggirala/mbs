package com.sivalabs.springapp;

import au.com.bytecode.opencsv.CSVReader;
import com.sivalabs.springapp.entities.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * User: duggirag
 * Date: 1/18/15
 * Time: 8:24 AM
 */
public class CsvFileUtils {
    public static void main(String a[]) throws IOException {
        CsvFileUtils csvFileUtils = new CsvFileUtils();
        csvFileUtils.run();
    }

    public void run() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\Users\\duggirag\\Downloads\\CustomerInfo.csv"));
        CSVReader csvReader = new CSVReader(bufferedReader);

        String[] lineReader = csvReader.readNext();
        int i = 0;
        while (lineReader != null) {
            String[] customerInfo = lineReader;
            if (i != 0 && customerInfo != null && customerInfo.length == 8) {
                User user = new User();
                user.setGivenSerialNumber(new Integer(customerInfo[0]));
                user.setSector(customerInfo[1]);
                user.setName(customerInfo[2]);
                user.setAddress1(customerInfo[3]);
                user.setDailyBmOrder(new Double(customerInfo[4]));
                user.setDailyCmOrder(new Double(customerInfo[5]));
                user.setBmPrice(new Double(customerInfo[6]));
                user.setCmPrice(new Double(customerInfo[7]));
                user.setActive(Boolean.TRUE);
                System.out.println(user);
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

    }
}
