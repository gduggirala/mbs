package com.sivalabs.springapp.web.controllers;

import com.sivalabs.springapp.entities.Bill;
import com.sivalabs.springapp.entities.DailyOrder;
import com.sivalabs.springapp.entities.User;
import com.sivalabs.springapp.services.BillService;
import com.sivalabs.springapp.services.UserService;
import jdk.nashorn.internal.ir.RuntimeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: gduggirala
 * Date: 26/12/14
 * Time: 2:05 PM
 */
@RestController
@RequestMapping("/rest/bill/")
public class BillResource {
    @Autowired
    private BillService billService;

    @Autowired
    private UserService userService;

    @RequestMapping(value="/", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, List<Bill>>> findAll()
    {
        List<Bill> bills = billService.findAllBills();
        Map<String, List<Bill>> billMap= new HashMap<>();
        billMap.put("bills",bills);
        return new ResponseEntity<>(billMap, HttpStatus.OK) ;
    }

    @RequestMapping(value="/user/{id}", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, List<Bill>>> findByUserId(@PathVariable Long id)
    {
        List<Bill> bills = billService.findByUserId(id);
        Map<String, List<Bill>> billMap= new HashMap<>();
        billMap.put("bills",bills);
        return new ResponseEntity<>(billMap, HttpStatus.OK) ;
    }

    @RequestMapping(value="/generate/user/{id}", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, List<Bill>>> generateUserBill(@PathVariable Long id)
    {
        User user = userService.findUserById(id);
        LocalDate localDate = LocalDate.now();
        Bill currentMonthBill = billService.generateUserBill(user,localDate.getMonth(), localDate.getYear());
        List<Bill> bills= new ArrayList<>(1);
        bills.add(currentMonthBill);
        Map<String, List<Bill>> billMap= new HashMap<>();
        billMap.put("bills",bills);
        return new ResponseEntity<>(billMap, HttpStatus.OK) ;
    }

    @RequestMapping(value="/regenerate/user/{id}", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, List<Bill>>> reGenerateUserBill(@PathVariable Long id)
    {
        Bill currentMonthBill = billService.recalculateUserBillByUser(id);
        List<Bill> bills= new ArrayList<>(1);
        bills.add(currentMonthBill);
        Map<String, List<Bill>> billMap= new HashMap<>();
        billMap.put("bills",bills);
        return new ResponseEntity<>(billMap, HttpStatus.OK) ;
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Bill> updateUser(@RequestBody Bill modifiedBill) {
        Bill actualBill = billService.findById(modifiedBill.getId());
        modifiedBill.setUser(actualBill.getUser());
        Bill bill = billService.update(modifiedBill);
        return new ResponseEntity<Bill>(bill, HttpStatus.OK);
    }

    @RequestMapping(value = "/generateAll", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public  ResponseEntity<Map<String, List<Bill>>> generateAllCustomerBills(@RequestParam("monthValue") Integer monthValue,
    @RequestParam("monthText") String month) {
        LocalDate now = LocalDate.now();
        Month month1 = Month.of(monthValue);
        List<Bill> billsList = billService.generateCustomerBills(month1, now.getYear());
        Map<String, List<Bill>> billMap= new HashMap<>();
        billMap.put("bills",billsList);
        return new ResponseEntity<>(billMap, HttpStatus.OK) ;
    }
}
