package com.zeyuli.service;


import java.util.Map;

/**
 * UserService接口
 *
 * @author 李泽聿
 * @since 2025-10-21 16:58
 */

public interface UserService {
    Map<String, Object> login(String username, String password);

    Map<String, Object> register(String username, String password);
}
