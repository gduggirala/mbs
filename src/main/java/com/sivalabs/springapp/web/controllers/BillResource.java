package com.sivalabs.springapp.web.controllers;

import com.sivalabs.springapp.entities.Bill;
import com.sivalabs.springapp.entities.DailyOrder;
import com.sivalabs.springapp.services.BillService;
import jdk.nashorn.internal.ir.RuntimeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
