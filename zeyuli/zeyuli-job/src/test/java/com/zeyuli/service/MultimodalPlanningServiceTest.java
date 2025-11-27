package com.zeyuli.service;

import com.zeyuli.pojo.vo.MultimodalInput;
import com.zeyuli.pojo.vo.MultimodalResult;
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
 * 行程多模态规划功能测试类
 * 
 * @author 李泽聿
 */
@ExtendWith(MockitoExtension.class)
public class MultimodalPlanningServiceTest {
    
    @InjectMocks
    private MultimodalPlanningServiceImpl multimodalPlanningService;
    
    @Mock
    private MapService mapService;
    
    @Mock
    private ItineraryService itineraryService;
    
    private MultimodalInput textInput;
    private MultimodalInput imageInput;
    
    @BeforeEach
    void setUp() {
        // 初始化文本输入
        textInput = new MultimodalInput();
        textInput.setInputType("text");
        textInput.setTextInput("帮我规划一条北京3日游的行程，预算1000元，喜欢历史文化景点");
        textInput.setUserId("test_user_1");
        
        // 设置基础参数
        Map<String, Object> basicParams = new HashMap<>();
        basicParams.put("city", "北京");
        basicParams.put("days", 3);
        basicParams.put("budget", 1000);
        basicParams.put("travelStyle", "历史文化");
        textInput.setBasicParams(basicParams);
        
        // 初始化图片输入
        imageInput = new MultimodalInput();
        imageInput.setInputType("image");
        imageInput.setUserId("test_user_2");
        
        // 设置图片信息
        Map<String, Object> imageInfo = new HashMap<>();
        imageInfo.put("imageUrl", "https://example.com/beijing_forbidden_city.jpg");
        imageInfo.put("imageDescription", "故宫");
        imageInput.setAdditionalInfo(imageInfo);
    }
    
    @Test
    void testPlanItineraryByMultimodal() {
        // 测试文本模式行程规划
        MultimodalResult textResult = multimodalPlanningService.planItineraryByMultimodal(textInput);
        
        assertNotNull(textResult);
        assertEquals("text", textResult.getInputType());
        assertNotNull(textResult.getItineraryResult());
        
        // 验证行程结果
        Map<String, Object> itinerary = textResult.getItineraryResult();
        assertTrue(itinerary.containsKey("city"));
        assertTrue(itinerary.containsKey("days"));
        assertTrue(itinerary.containsKey("budget"));
        assertTrue(itinerary.containsKey("travelStyle"));
        assertTrue(itinerary.containsKey("dailyItineraries"));
        
        // 测试图片模式行程规划
        MultimodalResult imageResult = multimodalPlanningService.planItineraryByMultimodal(imageInput);
        
        assertNotNull(imageResult);
        assertEquals("image", imageResult.getInputType());
        assertNotNull(imageResult.getItineraryResult());
    }
    
    @Test
    void testExtractTravelInfoFromImage() {
        // 测试从图片中提取旅行信息
        Map<String, Object> imageInfo = new HashMap<>();
        imageInfo.put("imageUrl", "https://example.com/beijing_forbidden_city.jpg");
        imageInfo.put("imageDescription", "故宫");
        
        Map<String, Object> extractedInfo = multimodalPlanningService.extractTravelInfoFromImage(imageInfo);
        
        assertNotNull(extractedInfo);
        assertTrue(extractedInfo.containsKey("attraction"));
        assertTrue(extractedInfo.containsKey("city"));
        assertTrue(extractedInfo.containsKey("category"));
    }
    
    @Test
    void testConvertVoiceToText() {
        // 测试语音转文本
        Map<String, Object> voiceInfo = new HashMap<>();
        voiceInfo.put("audioUrl", "https://example.com/voice_recording.mp3");
        voiceInfo.put("duration", 30);
        
        String text = multimodalPlanningService.convertVoiceToText(voiceInfo);
        
        assertNotNull(text);
        // 实际应用中，这里应该有更详细的验证
    }
    
    @Test
    void testProcessTextInput() {
        // 测试处理文本输入
        String textInput = "我想去上海玩两天，预算800元，喜欢购物和美食";
        Map<String, Object> processedInfo = multimodalPlanningService.processTextInput(textInput);
        
        assertNotNull(processedInfo);
        assertTrue(processedInfo.containsKey("city"));
        assertTrue(processedInfo.containsKey("days"));
        assertTrue(processedInfo.containsKey("budget"));
        assertTrue(processedInfo.containsKey("travelStyle"));
    }
    
    @Test
    void testGenerateMultimodalResponse() {
        // 测试生成多模态响应
        Map<String, Object> itineraryData = new HashMap<>();
        itineraryData.put("city", "北京");
        itineraryData.put("days", 3);
        itineraryData.put("dailyItineraries", new Object()); // 简化处理
        
        MultimodalResult result = multimodalPlanningService.generateMultimodalResponse(
            "text", itineraryData, "test_user_1");
        
        assertNotNull(result);
        assertEquals("text", result.getInputType());
        assertNotNull(result.getItineraryResult());
        assertNotNull(result.getRecommendedImages());
        assertNotNull(result.getVoiceDescriptions());
    }
    
    @Test
    void testValidateMultimodalInput() {
        // 测试验证多模态输入
        boolean valid = multimodalPlanningService.validateMultimodalInput(textInput);
        assertTrue(valid);
        
        // 测试无效输入
        MultimodalInput invalidInput = new MultimodalInput();
        invalidInput.setInputType("invalid_type"); // 无效的输入类型
        boolean invalid = multimodalPlanningService.validateMultimodalInput(invalidInput);
        assertFalse(invalid);
        
        // 测试缺少必要参数的输入
        MultimodalInput missingParamsInput = new MultimodalInput();
        missingParamsInput.setInputType("text");
        // 缺少文本内容
        boolean missingParams = multimodalPlanningService.validateMultimodalInput(missingParamsInput);
        assertFalse(missingParams);
    }
    
    @Test
    void testRecommendBasedOnVisualFeatures() {
        // 测试基于视觉特征的推荐
        Map<String, Object> visualFeatures = new HashMap<>();
        visualFeatures.put("landmark", "故宫");
        visualFeatures.put("sceneType", "历史建筑");
        visualFeatures.put("colorDominant", "red");
        
        Map<String, Object> recommendations = multimodalPlanningService.recommendBasedOnVisualFeatures(visualFeatures);
        
        assertNotNull(recommendations);
        assertTrue(recommendations.containsKey("attractions"));
        assertTrue(recommendations.containsKey("itinerarySuggestions"));
    }
    
    @Test
    void testOptimizeMultimodalItinerary() {
        // 测试优化多模态行程
        Map<String, Object> originalItinerary = new HashMap<>();
        originalItinerary.put("city", "北京");
        originalItinerary.put("days", 3);
        
        Map<String, Object> optimizationParams = new HashMap<>();
        optimizationParams.put("travelTime", "minimum");
        optimizationParams.put("priority", "popularity");
        
        Map<String, Object> optimizedItinerary = multimodalPlanningService.optimizeMultimodalItinerary(
            originalItinerary, optimizationParams);
        
        assertNotNull(optimizedItinerary);
        assertTrue(optimizedItinerary.containsKey("city"));
        assertTrue(optimizedItinerary.containsKey("days"));
        assertTrue(optimizedItinerary.containsKey("optimizationStatus"));
    }
    
    @Test
    void testProcessEmptyMultimodalInput() {
        // 测试处理空输入
        MultimodalInput emptyInput = new MultimodalInput();
        
        MultimodalResult result = multimodalPlanningService.planItineraryByMultimodal(emptyInput);
        
        assertNotNull(result);
        assertEquals("error", result.getResultStatus());
        assertNotNull(result.getErrorMessage());
    }
    
    @Test
    void testProcessInvalidTextInput() {
        // 测试处理无效文本输入
        MultimodalInput invalidTextInput = new MultimodalInput();
        invalidTextInput.setInputType("text");
        invalidTextInput.setTextInput(""); // 空文本
        
        MultimodalResult result = multimodalPlanningService.planItineraryByMultimodal(invalidTextInput);
        
        assertNotNull(result);
        assertEquals("error", result.getResultStatus());
    }
    
    @Test
    void testProcessInvalidImageInput() {
        // 测试处理无效图片输入
        MultimodalInput invalidImageInput = new MultimodalInput();
        invalidImageInput.setInputType("image");
        // 缺少图片信息
        
        MultimodalResult result = multimodalPlanningService.planItineraryByMultimodal(invalidImageInput);
        
        assertNotNull(result);
        assertEquals("error", result.getResultStatus());
    }
    
    @Test
    void testExtractTravelInfoFromComplexText() {
        // 测试从复杂文本中提取旅行信息
        String complexText = "我计划在下周五一和女朋友一起去杭州玩，大概3天左右，希望看看西湖和雷峰塔，预算两个人3000元，" +
                           "住的话希望离西湖近一点，交通方便，我们不喜欢太赶的行程，可以慢慢来。";
        
        Map<String, Object> extractedInfo = multimodalPlanningService.processTextInput(complexText);
        
        assertNotNull(extractedInfo);
        assertEquals("杭州", extractedInfo.get("city"));
        assertEquals(3, extractedInfo.get("days"));
        assertEquals(3000, extractedInfo.get("budget"));
        assertTrue(extractedInfo.containsKey("attractions"));
        assertTrue(extractedInfo.containsKey("accommodationPreference"));
    }
}