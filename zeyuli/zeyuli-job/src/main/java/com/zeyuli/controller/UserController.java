package com.zeyuli.controller;


import com.zeyuli.pojo.vo.RegisterVo;
import com.zeyuli.pojo.vo.UserVo;
import com.zeyuli.service.impl.UserServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 用户控制层
 *
 * @author 李泽聿
 * @since 2025-10-21 16:43
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @ApiOperation(value = "用户登录", notes = "用户登录接口")
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody UserVo vo) {
        return userService.login(vo.getUserName(), vo.getPassword());
    }

    @ApiOperation(value = "用户注册", notes = "用户注册接口")
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody RegisterVo vo) {
        return userService.register(vo.getUsername(), vo.getPassword());
    }
}
