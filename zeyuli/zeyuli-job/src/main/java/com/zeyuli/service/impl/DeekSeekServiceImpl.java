package com.zeyuli.service.impl;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 *
 * @author 李泽聿
 * @since 2025-10-21 15:13
 */
@Service
public class DeekSeekServiceImpl implements com.zeyuli.service.DeekSeekService {
    @Autowired
    private ChatClient chatClient;

    @Override
    public String Chat(String input) {
        return "";
    }
}
