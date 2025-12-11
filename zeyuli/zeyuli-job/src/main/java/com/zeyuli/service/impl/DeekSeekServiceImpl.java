package com.zeyuli.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeyuli.mappers.UserMapper;
import com.zeyuli.pojo.User;
import com.zeyuli.pojo.vo.FormatedMarkdownVo;
import com.zeyuli.pojo.vo.UserFormateVo;
import com.zeyuli.service.DeekSeekService;
import com.zeyuli.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
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
@Slf4j
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
    @Autowired
    private ChatModel chatModel;

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

    /**
     * JSON 格式化用户输入,见{@link #buildJsonFromMarkdownPrompt(String, String, String, LocalDate, LocalDate)}
     *
     * @param userInput : com.zeyuli.pojo.vo.UserFormateVo
     * @return : com.zeyuli.pojo.vo.FormatedMarkdownVo
     * @author : 李泽聿
     * @since : 2025-12-11 16:27
     */
    // todo 可能存在超时问题
    @Override
    public Mono<FormatedMarkdownVo> formatUserInput(UserFormateVo userInput) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            String realPrompt = buildJsonFromMarkdownPrompt(
                    userInput.getMarkdown(),
                    userInput.getStartCity(),
                    userInput.getEndCity(),
                    userInput.getStartDate(),
                    userInput.getEndDate()
            );

            log.info("发送请求到AI，提示词长度: {}", realPrompt.length());

            // 创建 Prompt 对象
            Prompt prompt = new Prompt(realPrompt);

            // 使用 ChatModel 调用
            return Mono.fromCallable(() -> {
                        log.info("开始AI调用...");
                        ChatResponse response = chatModel.call(prompt);

                        if (response == null || response.getResult() == null) {
                            throw new RuntimeException("AI 返回空响应");
                        }

                        String content = response.getResult().getOutput().getText();

                        if (content == null || content.trim().isEmpty()) {
                            throw new RuntimeException("AI 返回内容为空");
                        }

                        log.info("收到AI响应，长度: {}", content.length());
                        log.debug("响应内容预览: {}", content.substring(0, Math.min(200, content.length())));

                        // 清理 JSON 响应中的 markdown 代码块
                        String json = content.replaceFirst("(?is)^\\s*```(?:json)?\\s*", "")
                                .replaceFirst("(?is)\\s*```\\s*$", "")
                                .trim();

                        log.info("清理后的JSON长度: {}", json.length());
                        log.debug("JSON预览: {}", json.substring(0, Math.min(200, json.length())));

                        // 先尝试解析，确保JSON有效
                        FormatedMarkdownVo result = mapper.readValue(json, FormatedMarkdownVo.class);
                        log.info("JSON解析成功，包含 {} 天行程", result.getDays() != null ? result.getDays().size() : 0);
                        log.info("JSON解析成功，内容: {}", result);
                        return result;
                    })
                    .timeout(Duration.ofSeconds(120)) // 增加到120秒
                    .onErrorResume(e -> {
                        log.error("AI 调用失败: {}", e.getMessage(), e);
                        FormatedMarkdownVo errorResult = new FormatedMarkdownVo();
                        errorResult.setDays(new ArrayList<>());
                        return Mono.just(errorResult);
                    });

        } catch (Exception e) {
            log.error("构建请求失败: {}", e.getMessage(), e);
            return Mono.error(e);
        }
    }

    /**
     *
     * @param markdown  : 用户输入的 Markdown 文本
     * @param startCity : 出发城市
     * @param endCity   : 目的城市
     * @param startDate : 出发日期
     * @param endDate   : 返程日期
     * @return : java.lang.String
     * @author : 李泽聿
     * @since : 2025-12-11 16:28
     */
    private String buildJsonFromMarkdownPrompt(String markdown,
                                               String startCity,
                                               String endCity,
                                               LocalDate startDate,
                                               LocalDate endDate) {

        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startStr = startDate.format(fmt);
        String endStr = endDate.format(fmt);
        return String.format("""
                        请将以下旅行行程解析为JSON格式。
                        
                        要求：
                        1. 只返回JSON，不要任何额外文字
                        2. 使用以下格式：
                        {
                          "days": [
                            {
                              "dayIndex": 1,
                              "date": "yyyy-MM-dd",
                              "label": "第1天",
                              "items": [
                                {
                                  "time": "HH:mm",
                                  "title": "活动名称",
                                  "description": "简要说明",
                                  "attractions": 景点名称,
                                  "cost": 费用,
                                  "durationHours": 时长,
                                }
                              ]
                            }
                          ]
                        }
                        
                        如果某些信息不存在，使用合理估算值。
                        
                        行程信息：
                        - 出发地：%s
                        - 目的地：%s
                        - 出发日期：%s
                        - 返程日期：%s
                        - 行程天数：%d天
                        
                        行程内容：
                        %s
                        """,
                startCity, endCity, startStr, endStr, totalDays,
                markdown
        );
    }
}