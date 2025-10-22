package com.zeyuli.service.impl;

import com.zeyuli.mappers.UserMapper;
import com.zeyuli.pojo.User;
import com.zeyuli.service.DeekSeekService;
import com.zeyuli.util.JwtUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 调用ai业务层实现的接口
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
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    // 存储用户对话历史，key: userId, value: 对话消息列表
    private final Map<String, List<Message>> conversationHistory = new ConcurrentHashMap<>();

    // 最大历史记录条数，防止内存溢出
    private static final int MAX_HISTORY_SIZE = 20;

    @Override
    public Flux<String> chat(String userInput, String token, String startCity, String endCity, LocalDate startDate, LocalDate endDate) {
        String[] info = jwtUtil.getUserInfo(token);
        User res = userMapper.selectUserInfo(info[0]);
        String hash = DigestUtils.md5DigestAsHex(res.getPassword().getBytes()).substring(0, 6);

        if (!jwtUtil.isExpiration(token)
                && res.getId() != null
                && res.getUserName().equals(info[1])
                && hash.equals(info[2])) {

            String userId = info[0]; // 使用用户ID作为对话历史的key

            // 获取或创建用户对话历史
            List<Message> history = conversationHistory.computeIfAbsent(userId, k -> new ArrayList<>());

            // 构建用户消息
            String userMessageContent;
            if (userInput == null || userInput.trim().isEmpty()) {
                // 第一次请求：生成完整的旅行计划
                userMessageContent = buildInitialTravelPlanMessage(startCity, endCity, startDate, endDate);
            } else {
                // 后续请求：用户输入修改要求
                userMessageContent = buildUserInputMessage(userInput, startCity, endCity, startDate, endDate);
            }

            UserMessage userMessage = new UserMessage(userMessageContent);

            // 构建系统消息
            SystemPromptTemplate promptTemplate = new SystemPromptTemplate(role);
            Message systemMessage = promptTemplate.createMessage(Map.of("endCity", endCity));

            // 构建完整的消息列表：系统消息 + 历史记录 + 当前用户消息
            List<Message> messages = new ArrayList<>();
            messages.add(systemMessage);
            messages.addAll(history);
            messages.add(userMessage);

            // 创建Prompt
            Prompt prompt = new Prompt(messages);

            // 调用AI服务并获取响应
            Flux<String> response = chatClient.prompt(prompt)
                    .stream()
                    .content();

            // 收集完整的响应内容（用于保存到历史记录）
            StringBuilder fullResponse = new StringBuilder();

            return response
                    .doOnNext(fullResponse::append)
                    .doOnComplete(() -> {
                        // 对话完成后，将用户消息和AI响应添加到历史记录
                        if (!fullResponse.isEmpty()) {
                            // 添加用户消息到历史
                            history.add(userMessage);

                            // 添加AI响应到历史
                            history.add(new AssistantMessage(fullResponse.toString()));

                            // 限制历史记录大小，移除最旧的消息
                            while (history.size() > MAX_HISTORY_SIZE) {
                                // 保留系统消息，只移除用户和助手消息
                                if (history.size() > 2) { // 确保至少保留一对对话
                                    history.removeFirst(); // 移除最旧的用户消息
                                    history.removeFirst(); // 移除对应的助手消息
                                } else {
                                    break;
                                }
                            }
                        }
                    });
        }
        return Flux.just("身份验证失败，无法获取行程");
    }

    /**
     * 构建初始旅行计划查询消息
     */
    private String buildInitialTravelPlanMessage(String startCity, String endCity, LocalDate startDate, LocalDate endDate) {
        return """
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
                
                请提供详细、实用的旅行建议，并保持友好的语气。
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
    }

    /**
     * 构建用户输入修改要求的消息
     */
    private String buildUserInputMessage(String userInput, String startCity, String endCity, LocalDate startDate, LocalDate endDate) {
        return """
                基于之前的旅行对话历史，用户提出了以下修改要求：
                
                【用户修改要求】
                %s
                
                【旅行基本信息】
                - 出发地：%s
                - 目的地：%s
                - 出发日期：%s
                - 返回日期：%s
                
                请根据用户的要求，在原有旅行计划的基础上进行相应的调整和优化。如果用户的要求涉及到具体的行程安排、景点选择、住宿推荐等方面，请提供详细的修改建议。
                
                请保持友好的语气，并确保修改后的计划仍然符合旅行的时间和地点约束。
                """.formatted(
                userInput,
                startCity, endCity,
                startDate.format(DateTimeFormatter.ofPattern("yyyy年M月d日")),
                endDate.format(DateTimeFormatter.ofPattern("yyyy年M月d日"))
        );
    }

    /**
     * 清除用户的对话历史
     */
    @Override
    public void clearConversationHistory(String token) {
        String[] info = jwtUtil.getUserInfo(token);
        if (info != null && info.length > 0) {
            conversationHistory.remove(info[0]);
        }
    }

    /**
     * 获取用户对话历史大小（用于调试）
     */
    @Override
    public int getConversationHistorySize(String token) {
        String[] info = jwtUtil.getUserInfo(token);
        if (info != null && info.length > 0) {
            List<Message> history = conversationHistory.get(info[0]);
            return history != null ? history.size() : 0;
        }
        return 0;
    }
}