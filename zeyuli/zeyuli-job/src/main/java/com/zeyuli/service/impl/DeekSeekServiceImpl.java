package com.zeyuli.service.impl;


import com.zeyuli.service.DeekSeekService;
import com.zeyuli.util.Response;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Value("${DeekSeek.role}")
    private String role;

    @Autowired
    private ChatModel chatModel;

    public Flux<String> chat(String startCity, String endCity, LocalDate startDate, LocalDate endDate) {
        String message = """
                请为从%s到%s的旅行制定详细计划（出发：%s 返回：%s），需包含以下要素：
                
                【基础信息】
                - 出发地天气：%s同期气候特点
                - 目的地天气：%s实时天气预报
                - 两地交通：%s出发交通与%s到达交通方案
                
                【目的地特色】
                - 经济消费水平：%s物价指数说明
                - 必去景点：%s十大推荐景点
                - 特色美食：%s餐饮指南
                
                【行程规划】
                - 住宿推荐：%s高性价比住宿区域
                - 购物攻略：%s特色商品购买指南
                - 行程路线：%s到%s每日详细路线
                - 天气变化：%s每日天气变化预报
                """.formatted(
                startCity, endCity,
                startDate.format(DateTimeFormatter.ofPattern("yyyy年M月d日")),
                endDate.format(DateTimeFormatter.ofPattern("yyyy年M月d日")),
                startCity, endCity, startCity, endCity,
                endCity, endCity, endCity,
                endCity, endCity,
                startCity, endCity,
                endCity
        );
        UserMessage userMessage = new UserMessage(message);

        SystemPromptTemplate promptTemplate = new SystemPromptTemplate(role);
        Message systemMessage = promptTemplate.createMessage(Map.of("endCity", endCity));
        Prompt prompt = new Prompt(userMessage, systemMessage);
//        return chatModel.call(prompt)
//                .getResults().stream()
//                .map(x->x.getOutput().getContent()).collect(Collectors.joining(""));
        return chatClient.prompt().user(message).stream().content();
    }
}
