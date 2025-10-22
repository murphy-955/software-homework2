package com.zeyuli.controller;


import com.zeyuli.pojo.UserItineraryPlan;
import com.zeyuli.service.impl.DeekSeekServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Map;

/**
 *
 *
 * @author 李泽聿
 * @since 2025-10-21 15:02
 */

@RestController
@RequestMapping("/deekseek")
public class DeekSeekController {

    @Autowired
    private DeekSeekServiceImpl deekSeekService;

    @ApiOperation(value = "根据用户输入的出发地、目的地、出发日期、返回日期，生成旅行计划", notes = "根据用户输入的出发地、目的地、出发日期、返回日期，生成旅行计划")
    @GetMapping(path = "/planItinerary", produces = "text/html;charset=UTF-8")
    public Flux<String> chat(@RequestParam("startCity") String startCity,
                             @RequestParam("endCity") String endCity,
                             @RequestParam("startDate")LocalDate startDate,
                             @RequestParam("endDate") LocalDate endDate) {
        return deekSeekService.chat(startCity, endCity, startDate, endDate);
    }
}
