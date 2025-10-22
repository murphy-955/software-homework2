package com.zeyuli.controller;


import com.zeyuli.pojo.UserItineraryPlan;
import com.zeyuli.service.impl.DeekSeekServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

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

    @ApiOperation(value = "计划行程", notes = "根据用户输入的行程信息，生成行程计划")
    @GetMapping(path = "/planItinerary",produces = "text/HTML;charset=UTF-8")
    public Flux<String> chat(@RequestBody UserItineraryPlan plan) {
        return deekSeekService.chat(plan.getStartCity(), plan.getEndCity(), plan.getStartDate(), plan.getEndDate());
    }
}
