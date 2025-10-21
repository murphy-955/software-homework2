package com.zeyuli.controller;


import com.zeyuli.service.impl.DeekSeekServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 *
 *
 * @author 李泽聿
 * @since 2025-10-21 15:02
 */

@RestController
public class DeekSeekController {

    @Autowired
    private DeekSeekServiceImpl deekSeekService;

    @GetMapping("/chat")
    public Map<String, Object> chat(@RequestParam(value = "input", defaultValue = "hello") String input) {
        return deekSeekService.chat(input);
    }
}
