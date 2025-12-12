package com.zeyuli.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

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

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 存储用户对话历史，key: userId, value: 对话消息列表
    private final Map<String, List<Message>> conversationHistory = new ConcurrentHashMap<>();

    // 最大历史记录条数，防止内存溢出
    private static final int MAX_HISTORY_SIZE = 20;
    @Autowired
    private ChatModel chatModel;

    @Override
    public Flux<String> chat(String userInput,
                             String token,
                             String startCity,
                             String endCity,
                             LocalDate startDate,
                             LocalDate endDate) {

        /* ---------------- 1. 鉴权 ---------------- */
        String[] info = jwtUtil.getUserInfo(token);
        User res = userMapper.selectUserInfo(info[0]);
        String hash = DigestUtils.md5DigestAsHex(res.getPassword().getBytes()).substring(0, 6);

        if (jwtUtil.isExpiration(token)
                || res.getId() == null
                || !res.getUserName().equals(info[1])
                || !hash.equals(info[2])) {
            return Flux.just("{\"error\":\"身份验证失败，无法获取行程\"}");
        }

        /* ---------------- 2. 构造参数 ---------------- */
        Map<String, String> param = Map.of(
                "userInput", userInput == null ? "" : userInput,
                "startCity", startCity,
                "endCity", endCity,
                "startDate", startDate.toString(),
                "endDate", endDate.toString());

        // 将参数转为JSON字符串（Python脚本需要）
        String jsonParam;
        try {
            jsonParam = new ObjectMapper().writeValueAsString(param);
        } catch (JsonProcessingException e) {
            return Flux.error(new RuntimeException("参数序列化失败", e));
        }

        /* ---------------- 3. 启动Python进程并处理流 ---------------- */
        return Flux.create(sink -> {
            Process process;
            AtomicReference<Process> processRef = new AtomicReference<>();

            try {
                // 获取Python脚本路径（建议配置在application.properties）
                String scriptPath = "D:\\fzu\\Software\\homework2\\zeyuli\\zeyuli-job\\src\\main\\resources\\main.py";
                List<String> command = Arrays.asList("python", scriptPath);

                ProcessBuilder pb = new ProcessBuilder(command)
                        .redirectErrorStream(true); // 合并错误流到标准输出

                // 启动进程
                process = pb.start();
                processRef.set(process);

                // 写入参数到stdin
                try (OutputStream stdin = process.getOutputStream();
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin))) {
                    writer.write(jsonParam);
                    writer.flush(); // 必须flush确保数据写入
                }

                // 读取stdout流（关键：非阻塞处理）
                Thread outputThread = new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()))) {

                        String line;
                        while ((line = reader.readLine()) != null) {
                            // 处理Python脚本的特殊错误标记
                            if (line.startsWith("error: ")) {
                                sink.error(new RuntimeException(line.substring(7)));
                                return;
                            }
                            sink.next(line); // 发射数据片段
                        }

                        // 检查进程退出状态
                        int exitCode = process.waitFor();
                        if (exitCode != 0) {
                            sink.error(new RuntimeException("Python进程异常退出: " + exitCode));
                        } else {
                            sink.complete();
                        }
                    } catch (Exception e) {
                        if (!sink.isCancelled()) {
                            sink.error(new RuntimeException("流处理异常", e));
                        }
                    }
                });
                outputThread.setDaemon(true);
                outputThread.start();

                // 处理订阅取消
                sink.onDispose(() -> {
                    Process p = processRef.get();
                    if (p != null && p.isAlive()) {
                        p.destroyForcibly(); // 确保清理资源
                    }
                    if (!outputThread.isInterrupted()) {
                        outputThread.interrupt();
                    }
                });

            } catch (Exception e) {
                sink.error(new RuntimeException("进程启动失败", e));
            }
        }, FluxSink.OverflowStrategy.BUFFER); // 使用BUFFER避免背压问题
    }


    /* --------------------------------------------------
     * 下面 2 个 prompt 构造器强制 AI 输出指定 JSON 格式
     * -------------------------------------------------- */
    private String buildJsonTravelPrompt(String start, String end,
                                         LocalDate startDate, LocalDate endDate) {
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return String.format("""
                你是严格的 JSON 生成器，只返回合法 JSON，禁止任何注释、解释、markdown 代码块。
                若格式错误，用户将无法解析，视为严重事故。
                
                下面是一段已验证的范例，你必须保持完全相同的键名、嵌套深度、数据类型（String/Number/List），仅把内容替换成本次行程的真实信息。
                
                === 范例开始 ===
                {
                  "days": [
                    {
                      "dayIndex": 1,
                      "date": "2025-12-20",
                      "label": "第1天",
                      "items": [
                        {
                          "time": "09:00",
                          "title": "抵达厦门",
                          "description": "- 抵达 **厦门高崎机场**\\n- 乘坐 **机场快线** 前往市区\\n> 建议提前购买 **厦门公交卡**",
                          "attractions": "厦门高崎机场",
                          "cost": "约30元",
                          "durationHours": 1
                        },
                        {
                          "time": "14:00",
                          "title": "南普陀寺",
                          "description": "- 参观 **南普陀寺**\\n- 免费领香\\n- 登顶 **五老峰** 俯瞰厦大",
                          "attractions": "南普陀寺",
                          "cost": "0",
                          "durationHours": 2.5
                        }
                      ]
                    }
                  ]
                }
                === 范例结束 ===
                
                本次行程信息：
                - 出发地：%s
                - 目的地：%s
                - 出发日期：%s
                - 返程日期：%s
                - 总天数：%d
                
                严格按照范例的键名、结构、类型生成 JSON，description 字段允许使用 Markdown 排版。
                禁止输出范例之外的多余文字、禁止包裹 ```json、禁止注释。
                """, start, end, startDate.format(fmt), endDate.format(fmt), days);
    }

    private String buildJsonTravelPromptWithUserInput(String userInput,
                                                      String start, String end,
                                                      LocalDate startDate, LocalDate endDate) {
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return String.format("""
                你是资深旅行规划师。用户已对行程提出修改要求，请基于要求调整并重新输出完整 JSON。
                输出格式与字段要求与刚才完全相同，description 仍支持 Markdown。
                
                用户修改要求：
                %s
                
                行程天数：%d 天
                出发：%s → %s，%s 至 %s
                
                请直接返回 JSON，不要任何额外文字。
                """, userInput, days, start, end, startDate.format(fmt), endDate.format(fmt));
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
                        // 设置缓存
                        String id = jwtUtil.getUserInfo(userInput.getToken())[0].substring(0, 16);
                        String key = "user:formated:".concat(id);
                        redisTemplate.opsForValue().set(key, result, 24, TimeUnit.HOURS);
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
                        将以下旅行行程解析为JSON格式。
                        
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