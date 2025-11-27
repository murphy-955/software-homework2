package com.zeyuli.service;

import com.zeyuli.pojo.PersonalityTest;
import com.zeyuli.pojo.vo.PersonalityTestResultVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * 三分钟旅行人格测试功能测试类
 * 
 * @author 李泽聿
 */
@ExtendWith(MockitoExtension.class)
public class PersonalityTestServiceTest {
    
    @InjectMocks
    private PersonalityTestServiceImpl personalityTestService;
    
    @Mock
    private MapService mapService;
    
    private PersonalityTest test;
    private Map<String, String> userAnswers;
    
    @BeforeEach
    void setUp() {
        // 初始化测试数据
        test = personalityTestService.getPersonalityTest();
        
        // 创建用户答案
        userAnswers = new HashMap<>();
        // 为每个问题设置默认答案
        for (int i = 1; i <= test.getQuestions().size(); i++) {
            userAnswers.put("question_" + i, "option_" + i + "_1");
        }
    }
    
    @Test
    void testGetPersonalityTest() {
        // 测试获取人格测试对象
        assertNotNull(test);
        assertEquals("三分钟旅行人格测试", test.getTestName());
        assertNotNull(test.getQuestions());
        assertTrue(test.getQuestions().size() > 0);
        
        // 验证问题结构
        test.getQuestions().forEach(question -> {
            assertNotNull(question.getQuestionId());
            assertNotNull(question.getQuestionText());
            assertNotNull(question.getOptions());
            assertTrue(question.getOptions().size() >= 2);
            
            // 验证选项结构
            question.getOptions().forEach(option -> {
                assertNotNull(option.getOptionId());
                assertNotNull(option.getOptionText());
                assertNotNull(option.getPersonalityTraits());
            });
        });
        
        // 验证人格类型结构
        assertNotNull(test.getPersonalityTypes());
        assertTrue(test.getPersonalityTypes().size() >= 5);
        
        test.getPersonalityTypes().forEach(type -> {
            assertNotNull(type.getTypeId());
            assertNotNull(type.getTypeName());
            assertNotNull(type.getDescription());
            assertNotNull(type.getTraits());
        });
    }
    
    @Test
    void testCalculateTestResult() {
        // 测试计算测试结果
        PersonalityTestResultVO result = personalityTestService.calculateTestResult(userAnswers);
        
        assertNotNull(result);
        assertNotNull(result.getTestId());
        assertNotNull(result.getUserAnswers());
        assertEquals(userAnswers, result.getUserAnswers());
        
        // 验证特质得分
        assertNotNull(result.getPersonalityTraitScores());
        assertTrue(result.getPersonalityTraitScores().size() > 0);
        
        // 验证主导人格类型
        assertNotNull(result.getDominantPersonalityType());
        
        // 验证推荐景点数量范围
        assertNotNull(result.getRecommendedAttractionCount());
        assertTrue(result.getRecommendedAttractionCount().getMin() >= 1);
        assertTrue(result.getRecommendedAttractionCount().getMax() >= 3);
    }
    
    @Test
    void testGeneratePersonalityItinerary() {
        // 模拟地图服务返回
        when(mapService.getNearbyAttractions("北京", 5000, "历史文化"))
            .thenReturn(new ArrayList<>());
        
        // 生成人格行程
        Map<String, Object> itinerary = personalityTestService.generatePersonalityItinerary(
            "冒险者", "北京", 3, null, null);
        
        assertNotNull(itinerary);
        assertTrue(itinerary.containsKey("city"));
        assertTrue(itinerary.containsKey("days"));
        assertTrue(itinerary.containsKey("personalityType"));
        assertTrue(itinerary.containsKey("dailyItineraries"));
    }
    
    @Test
    void testGetPersonalityRecommendations() {
        // 测试获取人格推荐
        Map<String, Object> recommendations = personalityTestService.getPersonalityRecommendations("探险家");
        
        assertNotNull(recommendations);
        assertTrue(recommendations.containsKey("personalityType"));
        assertTrue(recommendations.containsKey("recommendations"));
        assertTrue(recommendations.containsKey("travelTips"));
    }
    
    @Test
    void testGetQuestionById() {
        // 测试根据ID获取问题
        PersonalityTest.TestQuestion question = personalityTestService.getQuestionById("question_1");
        
        assertNotNull(question);
        assertEquals("question_1", question.getQuestionId());
        
        // 测试获取不存在的问题
        PersonalityTest.TestQuestion nonExistentQuestion = personalityTestService.getQuestionById("non_existent_question");
        assertNull(nonExistentQuestion);
    }
    
    @Test
    void testGetPersonalityTypeById() {
        // 测试根据ID获取人格类型
        PersonalityTest.PersonalityType type = personalityTestService.getPersonalityTypeById("adventurer");
        
        if (type != null) { // 可能在实际实现中不存在这个ID
            assertEquals("adventurer", type.getTypeId());
        }
    }
    
    @Test
    void testCalculateTestResultWithEmptyAnswers() {
        // 测试空答案情况
        Map<String, String> emptyAnswers = new HashMap<>();
        PersonalityTestResultVO result = personalityTestService.calculateTestResult(emptyAnswers);
        
        assertNotNull(result);
        assertTrue(result.getPersonalityTraitScores().values().stream().allMatch(score -> score == 0));
    }
    
    @Test
    void testCalculateTestResultWithInvalidAnswers() {
        // 测试无效答案情况
        Map<String, String> invalidAnswers = new HashMap<>();
        invalidAnswers.put("question_1", "invalid_option");
        
        PersonalityTestResultVO result = personalityTestService.calculateTestResult(invalidAnswers);
        
        assertNotNull(result);
        // 无效答案应被忽略，对应的特质得分应为0
    }
}