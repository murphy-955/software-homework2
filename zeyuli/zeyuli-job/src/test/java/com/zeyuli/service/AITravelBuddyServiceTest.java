package com.zeyuli.service;

import com.zeyuli.pojo.ChatMessage;
import com.zeyuli.pojo.vo.ItineraryPlanVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * AI旅行搭子聊天模式功能测试类
 * 
 * @author 李泽聿
 */
@ExtendWith(MockitoExtension.class)
public class AITravelBuddyServiceTest {
    
    @InjectMocks
    private AITravelBuddyServiceImpl aiTravelBuddyService;
    
    @Mock
    private MapService mapService;
    
    @Mock
    private ItineraryService itineraryService;
    
    private ChatMessage userMessage;
    private ItineraryPlanVO mockItinerary;
    
    @BeforeEach
    void setUp() {
        // 初始化用户消息
        userMessage = new ChatMessage();
        userMessage.setMessageId("USER_" + UUID.randomUUID().toString());
        userMessage.setSessionId("SESSION_TEST_123");
        userMessage.setSenderType("user");
        userMessage.setContent("推荐一下北京的热门景点");
        userMessage.setMessageType("text");
        userMessage.setSendTime(new Date());
        userMessage.setStatus("sent");
        
        // 初始化模拟行程
        mockItinerary = new ItineraryPlanVO();
        mockItinerary.setCity("北京");
        mockItinerary.setDays(3);
        mockItinerary.setTravelStyle("历史文化");
        mockItinerary.setAverageCost(1000.0);
        
        // 创建每日行程
        List<ItineraryPlanVO.DailyItinerary> dailyItineraries = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            ItineraryPlanVO.DailyItinerary day = new ItineraryPlanVO.DailyItinerary();
            day.setDayNumber(i);
            dailyItineraries.add(day);
        }
        mockItinerary.setDailyItineraries(dailyItineraries);
    }
    
    @Test
    void testProcessUserMessage() {
        // 测试处理用户消息
        ChatMessage response = aiTravelBuddyService.processUserMessage(userMessage);
        
        assertNotNull(response);
        assertEquals("ai", response.getSenderType());
        assertNotNull(response.getContent());
        assertEquals("recommendation", response.getMessageType());
        
        // 验证消息ID和会话ID
        assertNotNull(response.getMessageId());
        assertEquals(userMessage.getSessionId(), response.getSessionId());
        
        // 验证回复关系
        assertEquals(userMessage.getMessageId(), response.getReplyToMessageId());
    }
    
    @Test
    void testCreateNewSession() {
        // 测试创建新会话
        String userId = "test_user_456";
        String initialMessage = "你好，我想去旅行";
        Map<String, Object> preferences = new HashMap<>();
        preferences.put("language", "中文");
        
        String sessionId = aiTravelBuddyService.createNewSession(userId, initialMessage, preferences);
        
        assertNotNull(sessionId);
        assertTrue(sessionId.startsWith("SESSION_"));
    }
    
    @Test
    void testGetSessionHistory() {
        // 先创建会话并发送消息
        String sessionId = "TEST_SESSION_HISTORY";
        ChatMessage testMessage = new ChatMessage();
        testMessage.setMessageId("TEST_MSG_1");
        testMessage.setSessionId(sessionId);
        testMessage.setSenderType("user");
        testMessage.setContent("测试消息");
        testMessage.setSendTime(new Date());
        
        // 处理消息以创建会话历史
        aiTravelBuddyService.processUserMessage(testMessage);
        
        // 获取会话历史
        List<ChatMessage> history = aiTravelBuddyService.getSessionHistory(sessionId, 10, 0);
        
        assertNotNull(history);
        assertTrue(history.size() >= 2); // 至少有用户消息和AI回复
    }
    
    @Test
    void testProvideIntelligentSuggestion() {
        // 测试提供智能建议
        String userId = "test_user_789";
        Map<String, Double> location = new HashMap<>();
        location.put("latitude", 39.9042);
        location.put("longitude", 116.4074);
        String currentTime = "14:30";
        
        Map<String, Object> context = new HashMap<>();
        context.put("weather", "sunny");
        
        ChatMessage suggestion = aiTravelBuddyService.provideIntelligentSuggestion(userId, location, currentTime, context);
        
        assertNotNull(suggestion);
        assertEquals("ai", suggestion.getSenderType());
        assertEquals("recommendation", suggestion.getMessageType());
        assertNotNull(suggestion.getContent());
        assertNotNull(suggestion.getLocation());
    }
    
    @Test
    void testRecommendAttractions() {
        // 测试推荐景点
        String query = "北京故宫附近的景点";
        Map<String, Double> location = new HashMap<>();
        location.put("latitude", 39.9163);
        location.put("longitude", 116.3972);
        
        Map<String, Object> preferences = new HashMap<>();
        preferences.put("budget", 500);
        
        ChatMessage recommendation = aiTravelBuddyService.recommendAttractions(query, location, preferences, 3);
        
        assertNotNull(recommendation);
        assertEquals("ai", recommendation.getSenderType());
        assertEquals("recommendation", recommendation.getMessageType());
        assertNotNull(recommendation.getContent());
    }
    
    @Test
    void testProvideEmotionalSupport() {
        // 测试积极情绪支持
        String userId = "test_user_emotional";
        List<ChatMessage> recentMessages = new ArrayList<>();
        
        ChatMessage positiveMessage = new ChatMessage();
        positiveMessage.setContent("今天玩得很开心！景色太美了");
        recentMessages.add(positiveMessage);
        
        ChatMessage positiveSupport = aiTravelBuddyService.provideEmotionalSupport(userId, 0.8, recentMessages);
        
        assertNotNull(positiveSupport);
        assertEquals("ai", positiveSupport.getSenderType());
        assertNotNull(positiveSupport.getContent());
        assertTrue(positiveSupport.getContent().contains("开心"));
        
        // 测试消极情绪支持
        ChatMessage negativeSupport = aiTravelBuddyService.provideEmotionalSupport(userId, -0.5, recentMessages);
        
        assertNotNull(negativeSupport);
        assertTrue(negativeSupport.getContent().contains("分享"));
    }
    
    @Test
    void testAnswerLocationQuestion() {
        // 测试回答地点问题
        String location = "故宫";
        String question = "故宫怎么去？";
        String language = "中文";
        
        ChatMessage answer = aiTravelBuddyService.answerLocationQuestion(location, question, language);
        
        assertNotNull(answer);
        assertEquals("ai", answer.getSenderType());
        assertEquals("information", answer.getMessageType());
        assertNotNull(answer.getContent());
        assertTrue(answer.getContent().contains(location));
        assertTrue(answer.getContent().contains("交通"));
    }
    
    @Test
    void testGenerateTripSummary() {
        // 测试生成旅行总结
        String userId = "test_user_summary";
        
        ChatMessage summary = aiTravelBuddyService.generateTripSummary(userId, mockItinerary, false);
        
        assertNotNull(summary);
        assertEquals("ai", summary.getSenderType());
        assertEquals("text", summary.getMessageType());
        assertNotNull(summary.getContent());
        assertTrue(summary.getContent().contains(mockItinerary.getCity()));
        assertTrue(summary.getContent().contains(String.valueOf(mockItinerary.getDays())));
    }
    
    @Test
    void testGetUserChatPreferences() {
        // 测试获取用户聊天偏好
        String userId = "test_user_preferences";
        
        // 先设置偏好
        Map<String, Object> preferences = new HashMap<>();
        preferences.put("preferred_language", "中文");
        preferences.put("response_style", "balanced");
        aiTravelBuddyService.updateUserChatPreferences(userId, preferences);
        
        // 获取偏好
        Map<String, Object> retrievedPrefs = aiTravelBuddyService.getUserChatPreferences(userId);
        
        assertNotNull(retrievedPrefs);
        assertEquals("中文", retrievedPrefs.get("preferred_language"));
        assertEquals("balanced", retrievedPrefs.get("response_style"));
    }
    
    @Test
    void testUpdateUserChatPreferences() {
        // 测试更新用户聊天偏好
        String userId = "test_user_update_prefs";
        Map<String, Object> newPreferences = new HashMap<>();
        newPreferences.put("preferred_language", "English");
        newPreferences.put("response_style", "detailed");
        newPreferences.put("notification_enabled", true);
        
        Map<String, Object> updatedPrefs = aiTravelBuddyService.updateUserChatPreferences(userId, newPreferences);
        
        assertNotNull(updatedPrefs);
        assertEquals("English", updatedPrefs.get("preferred_language"));
        assertEquals("detailed", updatedPrefs.get("response_style"));
        assertEquals(true, updatedPrefs.get("notification_enabled"));
    }
    
    @Test
    void testSetBuddyPersonality() {
        // 测试设置AI性格
        String sessionId = "TEST_SESSION_PERSONALITY";
        Map<String, String> personalityTraits = new HashMap<>();
        personalityTraits.put("personality", "友好活泼");
        personalityTraits.put("response_style", "detailed");
        
        boolean success = aiTravelBuddyService.setBuddyPersonality(sessionId, personalityTraits);
        
        assertTrue(success);
        
        // 测试无效性格类型
        Map<String, String> invalidPersonality = new HashMap<>();
        invalidPersonality.put("personality", "不存在的性格类型");
        
        boolean failure = aiTravelBuddyService.setBuddyPersonality("INVALID_SESSION", invalidPersonality);
        assertFalse(failure);
    }
    
    @Test
    void testGetAvailablePersonalities() {
        // 测试获取可用性格类型
        Set<String> personalities = aiTravelBuddyService.getAvailablePersonalities();
        
        assertNotNull(personalities);
        assertFalse(personalities.isEmpty());
        assertTrue(personalities.contains("友好活泼"));
        assertTrue(personalities.contains("专业知识型"));
        assertTrue(personalities.contains("幽默风趣"));
    }
    
    @Test
    void testProcessMultimodalInput() {
        // 测试处理多模态输入（图片）
        ChatMessage imageMessage = new ChatMessage();
        imageMessage.setMessageId("IMAGE_" + UUID.randomUUID().toString());
        imageMessage.setSessionId("SESSION_MULTIMODAL");
        imageMessage.setSenderType("user");
        imageMessage.setMessageType("image");
        imageMessage.setSendTime(new Date());
        
        // 设置图片信息
        Map<String, Object> imageInfo = new HashMap<>();
        imageInfo.put("imageUrl", "https://example.com/image.jpg");
        imageMessage.setAdditionalInfo(imageInfo);
        
        ChatMessage response = aiTravelBuddyService.processMultimodalInput(imageMessage);
        
        assertNotNull(response);
        assertEquals("ai", response.getSenderType());
        assertNotNull(response.getContent());
    }
    
    @Test
    void testGenerateWeatherAlert() {
        // 测试生成天气提醒
        Map<String, Double> location = new HashMap<>();
        location.put("latitude", 39.9042);
        location.put("longitude", 116.4074);
        
        ChatMessage weatherAlert = aiTravelBuddyService.generateWeatherAlert(location, mockItinerary);
        
        assertNotNull(weatherAlert);
        assertEquals("ai", weatherAlert.getSenderType());
        assertEquals("system", weatherAlert.getMessageType());
        assertNotNull(weatherAlert.getContent());
        assertTrue(weatherAlert.getContent().contains("天气"));
        assertTrue(weatherAlert.getContent().contains(mockItinerary.getCity()));
    }
    
    @Test
    void testCloseSession() {
        // 测试关闭会话
        String sessionId = "SESSION_TO_CLOSE";
        
        // 先创建会话
        aiTravelBuddyService.createNewSession("test_user_close", "测试关闭会话", null);
        
        // 关闭会话
        boolean success = aiTravelBuddyService.closeSession(sessionId);
        
        assertTrue(success);
        
        // 尝试关闭不存在的会话
        boolean failure = aiTravelBuddyService.closeSession("NON_EXISTENT_SESSION");
        assertFalse(failure);
    }
    
    @Test
    void testGetSessionStatistics() {
        // 测试获取会话统计信息
        String sessionId = "SESSION_STATISTICS";
        
        // 先创建会话并发送消息
        ChatMessage testMsg = new ChatMessage();
        testMsg.setMessageId("STAT_MSG_1");
        testMsg.setSessionId(sessionId);
        testMsg.setSenderType("user");
        testMsg.setContent("测试统计");
        testMsg.setSendTime(new Date());
        
        aiTravelBuddyService.processUserMessage(testMsg);
        
        // 获取统计信息
        Map<String, Object> stats = aiTravelBuddyService.getSessionStatistics(sessionId);
        
        assertNotNull(stats);
        assertTrue(stats.containsKey("total_messages"));
        assertTrue(stats.containsKey("user_messages"));
        assertTrue(stats.containsKey("ai_messages"));
        assertTrue(stats.containsKey("session_duration_ms"));
    }
    
    @Test
    void testHandleEmergency() {
        // 测试处理紧急情况
        String userId = "test_user_emergency";
        String emergencyType = "health";
        Map<String, Double> location = new HashMap<>();
        location.put("latitude", 39.9042);
        location.put("longitude", 116.4074);
        
        ChatMessage emergencyResponse = aiTravelBuddyService.handleEmergency(userId, emergencyType, location);
        
        assertNotNull(emergencyResponse);
        assertEquals("system", emergencyResponse.getSenderType());
        assertEquals("system", emergencyResponse.getMessageType());
        assertNotNull(emergencyResponse.getContent());
        assertTrue(emergencyResponse.getContent().contains("紧急"));
        assertTrue(emergencyResponse.getContent().contains(emergencyType));
    }
    
    @Test
    void testProcessSpecialKeywords() {
        // 测试处理特殊关键词
        userMessage.setContent("我好累啊");
        ChatMessage response = aiTravelBuddyService.processUserMessage(userMessage);
        
        assertNotNull(response);
        assertTrue(response.getContent().contains("休息"));
        
        userMessage.setContent("门票太贵了");
        response = aiTravelBuddyService.processUserMessage(userMessage);
        
        assertNotNull(response);
        assertTrue(response.getContent().contains("性价比"));
    }
    
    @Test
    void testErrorHandling() {
        // 测试错误处理
        ChatMessage invalidMessage = new ChatMessage();
        invalidMessage.setMessageId("ERROR_TEST");
        invalidMessage.setSessionId("SESSION_ERROR");
        invalidMessage.setSenderType("user");
        invalidMessage.setContent("这是一条会导致错误的消息");
        
        // 这里期望服务能正确处理异常并返回错误消息
        try {
            ChatMessage response = aiTravelBuddyService.processUserMessage(invalidMessage);
            assertNotNull(response);
            assertTrue(response.getStatus().equals("error") || response.getStatus().equals("sent"));
        } catch (Exception e) {
            fail("服务应该能够处理异常而不抛出");
        }
    }
}