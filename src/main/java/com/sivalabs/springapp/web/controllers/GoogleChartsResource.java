package com.sivalabs.springapp.web.controllers;

import com.sivalabs.springapp.entities.DailyOrder;
import com.sivalabs.springapp.services.DailyOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: duggirag
 * Date: 1/30/15
 * Time: 6:39 PM
 */
@RestController
@RequestMapping("/rest/google/charts")
public class GoogleChartsResource {
    @Autowired
    DailyOrderService dailyOrderService;

    @RequestMapping(value = "/googleCharts", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView findAll(ModelAndView modelAndView) {
        modelAndView.setViewName("/googleCharts");
        List<DailyOrder> dailyOrders = dailyOrderService.findAllDailyOrders();
        Map<String, List<DailyOrder>> dailyOrderMap = new HashMap<>();
        dailyOrderMap.put("dailyOrders", dailyOrders);
        return modelAndView;
    }

}
