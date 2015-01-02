package com.sivalabs.springapp.web.controllers;

import com.sivalabs.springapp.entities.Bill;
import com.sivalabs.springapp.entities.DailyOrder;
import com.sivalabs.springapp.services.BillService;
import com.sivalabs.springapp.services.DailyOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: giridhad
 * Date: 12/19/14
 * Time: 8:09 PM
 */
@RestController
@RequestMapping("/rest/dailyOrders/")
public class DailyOrderResource {
    @Autowired
    DailyOrderService dailyOrderService;
    @Autowired
    BillService billService;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, List<DailyOrder>>> findAll() {
        List<DailyOrder> dailyOrders = dailyOrderService.findAllDailyOrders();
        Map<String, List<DailyOrder>> dailyOrderMap = new HashMap<>();
        dailyOrderMap.put("dailyOrders", dailyOrders);
        return new ResponseEntity<>(dailyOrderMap, HttpStatus.OK);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, List<DailyOrder>>> findAllByUserId(@RequestParam("userId") Long id) {
        List<DailyOrder> dailyOrders = dailyOrderService.findByUserId(id);
        Map<String, List<DailyOrder>> dailyOrderMap = new HashMap<>();
        dailyOrderMap.put("dailyOrders", dailyOrders);
        return new ResponseEntity<>(dailyOrderMap, HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<DailyOrder> updateUser(@RequestBody DailyOrder modifiedDailyOrder) {
        DailyOrder actualDailyOrder = dailyOrderService.findById(modifiedDailyOrder.getId());
        modifiedDailyOrder.setUser(actualDailyOrder.getUser());
        DailyOrder savedDailyOrder = dailyOrderService.update(modifiedDailyOrder);
        Date orderDate = modifiedDailyOrder.getOrderDate();
        LocalDate localDate = orderDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Bill bill = billService.getBillForTheMonth(actualDailyOrder.getUser(), localDate);
        if (bill != null) {
            billService.recalculateUserBill(bill);
        }
        return new ResponseEntity<DailyOrder>(savedDailyOrder, HttpStatus.OK);
    }

}
