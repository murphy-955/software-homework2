package com.zeyuli.service.impl;


import com.zeyuli.enm.StatusCodeEnum;
import com.zeyuli.mappers.UserMapper;
import com.zeyuli.pojo.User;
import com.zeyuli.service.UserService;
import com.zeyuli.util.JwtUtil;
import com.zeyuli.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author 李泽聿
 * @since 2025-10-21 16:58
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 用户密码
     * @return : java.util.Map<java.lang.String,java.lang.Object> 成功返回：<pre>
     *     {@code
     *      {
     *       "data": {
     *         "token": "token"
     *         },
     *       "message": "成功",
     *       "statusCode": 200
     *    }
     *     }
     * </pre>
     * <br>
     * 失败返回：<pre>
     *     {@code
     *      {
     *         "data": null,
     *         "message": "登录失败",
     *         "statusCode": 420
     *      }
     *     }
     * </pre>
     * @author : 李泽聿
     * @since : 2025-10-21 21:48
     */
    @Override
    public Map<String, Object> login(String username, String password) {
        User res = userMapper.login(username, password);
        if (res != null) {
            System.out.println(res);
            String token = jwtUtil.createToken(res.getId(), res.getUserName(), res.getPassword());
            HashMap<String, Object> data = new HashMap<>();
            data.put("token", token);
            return Response.success(data);
        }
        return Response.failed(StatusCodeEnum.LOGIN_FAILED);
    }
}
