package com.zeyuli.service;

import com.zeyuli.pojo.StudentRoute;
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
 * 学生专属线路库功能测试类
 * 
 * @author 李泽聿
 */
@ExtendWith(MockitoExtension.class)
public class StudentRouteServiceTest {
    
    @InjectMocks
    private StudentRouteServiceImpl studentRouteService;
    
    @Mock
    private MapService mapService;
    
    private Map<String, Object> filters;
    
    @BeforeEach
    void setUp() {
        // 初始化过滤器
        filters = new HashMap<>();
    }
    
    @Test
    void testGetRecommendedRoutes() {
        // 测试获取推荐线路
        List<StudentRoute> routes = studentRouteService.getRecommendedRoutes(filters);
        
        assertNotNull(routes);
        assertTrue(routes.size() >= 1);
        
        // 验证线路结构
        routes.forEach(route -> {
            validateRouteStructure(route);
        });
    }
    
    @Test
    void testSearchRoutes() {
        // 测试搜索线路
        filters.put("city", "北京");
        List<StudentRoute> routes = studentRouteService.searchRoutes(filters);
        
        assertNotNull(routes);
        // 不强制要求有结果，因为可能没有北京的线路
        
        if (!routes.isEmpty()) {
            routes.forEach(route -> {
                validateRouteStructure(route);
                // 如果有结果，应该都是北京的线路
                assertEquals("北京", route.getCity());
            });
        }
    }
    
    @Test
    void testGetRouteById() {
        // 测试根据ID获取线路
        List<StudentRoute> allRoutes = studentRouteService.getRecommendedRoutes(new HashMap<>());
        
        if (!allRoutes.isEmpty()) {
            String routeId = allRoutes.get(0).getRouteId();
            StudentRoute route = studentRouteService.getRouteById(routeId);
            
            assertNotNull(route);
            assertEquals(routeId, route.getRouteId());
            validateRouteStructure(route);
        }
        
        // 测试获取不存在的线路
        StudentRoute nonExistentRoute = studentRouteService.getRouteById("non_existent_route");
        assertNull(nonExistentRoute);
    }
    
    @Test
    void testGetRoutesByCity() {
        // 测试根据城市获取线路
        List<String> cities = Arrays.asList("北京", "上海", "广州");
        
        for (String city : cities) {
            List<StudentRoute> routes = studentRouteService.getRoutesByCity(city);
            
            assertNotNull(routes);
            
            routes.forEach(route -> {
                validateRouteStructure(route);
                assertEquals(city, route.getCity());
            });
        }
    }
    
    @Test
    void testGetRoutesByBudgetRange() {
        // 测试根据预算范围获取线路
        double minBudget = 500;
        double maxBudget = 2000;
        
        List<StudentRoute> routes = studentRouteService.getRoutesByBudgetRange(minBudget, maxBudget);
        
        assertNotNull(routes);
        
        routes.forEach(route -> {
            validateRouteStructure(route);
            double budget = route.getEstimatedBudget();
            assertTrue(budget >= minBudget && budget <= maxBudget);
        });
    }
    
    @Test
    void testGetRoutesByPersonalityType() {
        // 测试根据人格类型获取线路
        List<String> personalityTypes = Arrays.asList("冒险者", "探险家", "文化体验者");
        
        for (String type : personalityTypes) {
            List<StudentRoute> routes = studentRouteService.getRoutesByPersonalityType(type);
            
            assertNotNull(routes);
            
            routes.forEach(route -> {
                validateRouteStructure(route);
                assertTrue(route.getSuitablePersonalityTypes().contains(type));
            });
        }
    }
    
    @Test
    void testGetStudentDiscountInfo() {
        // 测试获取学生优惠信息
        List<StudentRoute> allRoutes = studentRouteService.getRecommendedRoutes(new HashMap<>());
        
        if (!allRoutes.isEmpty()) {
            String routeId = allRoutes.get(0).getRouteId();
            StudentRoute.DiscountInfo discountInfo = studentRouteService.getStudentDiscountInfo(routeId);
            
            assertNotNull(discountInfo);
            validateDiscountInfo(discountInfo);
        }
    }
    
    @Test
    void testGetRouteAttractions() {
        // 测试获取线路景点
        List<StudentRoute> allRoutes = studentRouteService.getRecommendedRoutes(new HashMap<>());
        
        if (!allRoutes.isEmpty()) {
            String routeId = allRoutes.get(0).getRouteId();
            List<StudentRoute.Attraction> attractions = studentRouteService.getRouteAttractions(routeId);
            
            assertNotNull(attractions);
            assertFalse(attractions.isEmpty());
            
            attractions.forEach(attraction -> {
                validateAttraction(attraction);
            });
        }
    }
    
    @Test
    void testGetPopularRoutes() {
        // 测试获取热门线路
        List<StudentRoute> routes = studentRouteService.getPopularRoutes(5);
        
        assertNotNull(routes);
        assertTrue(routes.size() <= 5);
        
        routes.forEach(route -> {
            validateRouteStructure(route);
        });
    }
    
    @Test
    void testGetRouteDailySchedule() {
        // 测试获取线路日程安排
        List<StudentRoute> allRoutes = studentRouteService.getRecommendedRoutes(new HashMap<>());
        
        if (!allRoutes.isEmpty()) {
            String routeId = allRoutes.get(0).getRouteId();
            List<StudentRoute.DailySchedule> schedules = studentRouteService.getRouteDailySchedule(routeId);
            
            assertNotNull(schedules);
            assertFalse(schedules.isEmpty());
            
            schedules.forEach(schedule -> {
                validateDailySchedule(schedule);
            });
        }
    }
    
    @Test
    void testGetRoutesByTravelDuration() {
        // 测试根据旅行时长获取线路
        int days = 2;
        List<StudentRoute> routes = studentRouteService.getRoutesByTravelDuration(days);
        
        assertNotNull(routes);
        
        routes.forEach(route -> {
            validateRouteStructure(route);
            assertEquals(days, route.getDays());
        });
    }
    
    @Test
    void testFilterRoutesByTransportation() {
        // 测试根据交通方式过滤线路
        List<String> transportations = Arrays.asList("地铁", "公交");
        
        List<StudentRoute> routes = studentRouteService.filterRoutesByTransportation(transportations);
        
        assertNotNull(routes);
        
        routes.forEach(route -> {
            validateRouteStructure(route);
            assertNotNull(route.getTransportationInfo());
        });
    }
    
    @Test
    void testGetRouteAverageRating() {
        // 测试获取线路平均评分
        List<StudentRoute> allRoutes = studentRouteService.getRecommendedRoutes(new HashMap<>());
        
        if (!allRoutes.isEmpty()) {
            String routeId = allRoutes.get(0).getRouteId();
            double rating = studentRouteService.getRouteAverageRating(routeId);
            
            assertTrue(rating >= 0 && rating <= 5);
        }
    }
    
    @Test
    void testGetSeasonalRoutes() {
        // 测试获取季节性线路
        String season = "春季";
        List<StudentRoute> routes = studentRouteService.getSeasonalRoutes(season);
        
        assertNotNull(routes);
        
        routes.forEach(route -> {
            validateRouteStructure(route);
            assertTrue(route.getBestSeasons().contains(season));
        });
    }
    
    @Test
    void testGetCustomizedRoutes() {
        // 测试获取定制线路
        Map<String, Object> customization = new HashMap<>();
        customization.put("city", "北京");
        customization.put("days", 3);
        customization.put("budget", 1500);
        
        List<StudentRoute> routes = studentRouteService.getCustomizedRoutes(customization);
        
        assertNotNull(routes);
        
        routes.forEach(route -> {
            validateRouteStructure(route);
        });
    }
    
    // 辅助方法：验证线路结构
    private void validateRouteStructure(StudentRoute route) {
        assertNotNull(route.getRouteId());
        assertNotNull(route.getRouteName());
        assertNotNull(route.getCity());
        assertNotNull(route.getDescription());
        assertTrue(route.getDays() > 0);
        assertTrue(route.getEstimatedBudget() >= 0);
        
        // 验证景点列表
        assertNotNull(route.getAttractions());
        assertFalse(route.getAttractions().isEmpty());
        
        // 验证学生优惠信息
        assertNotNull(route.getStudentDiscountInfo());
        validateDiscountInfo(route.getStudentDiscountInfo());
        
        // 验证适合的人格类型
        assertNotNull(route.getSuitablePersonalityTypes());
        assertFalse(route.getSuitablePersonalityTypes().isEmpty());
        
        // 验证最佳季节
        assertNotNull(route.getBestSeasons());
        assertFalse(route.getBestSeasons().isEmpty());
    }
    
    // 辅助方法：验证优惠信息
    private void validateDiscountInfo(StudentRoute.DiscountInfo discountInfo) {
        assertNotNull(discountInfo.getDiscountPercentage());
        assertNotNull(discountInfo.getRequiredDocuments());
        assertNotNull(discountInfo.getValidityPeriod());
        assertNotNull(discountInfo.getSpecialOffers());
    }
    
    // 辅助方法：验证景点信息
    private void validateAttraction(StudentRoute.Attraction attraction) {
        assertNotNull(attraction.getAttractionId());
        assertNotNull(attraction.getAttractionName());
        assertNotNull(attraction.getDescription());
        assertNotNull(attraction.getCategory());
        assertTrue(attraction.getRating() >= 0 && attraction.getRating() <= 5);
    }
    
    // 辅助方法：验证日程安排
    private void validateDailySchedule(StudentRoute.DailySchedule schedule) {
        assertTrue(schedule.getDay() > 0);
        assertNotNull(schedule.getActivities());
        assertFalse(schedule.getActivities().isEmpty());
        
        schedule.getActivities().forEach(activity -> {
            assertNotNull(activity.getTime());
            assertNotNull(activity.getActivityName());
            assertNotNull(activity.getDescription());
        });
    }
}