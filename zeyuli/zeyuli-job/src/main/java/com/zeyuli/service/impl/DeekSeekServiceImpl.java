package com.zeyuli.service.impl;


import com.zeyuli.service.DeekSeekService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author 李泽聿
 * @since 2025-10-21 15:13
 */
@Service
public class DeekSeekServiceImpl implements DeekSeekService {
    @Autowired
    private ChatClient chatClient;

    @Override
    public Map<String, Object> chat(String input) {
        return new HashMap<>();
    }
}
