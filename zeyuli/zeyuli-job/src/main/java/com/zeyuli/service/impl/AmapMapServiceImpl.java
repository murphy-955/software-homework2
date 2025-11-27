package com.zeyuli.service.impl;

import com.zeyuli.pojo.bo.Location;
import com.zeyuli.pojo.bo.POI;
import com.zeyuli.pojo.bo.Point;
import com.zeyuli.pojo.bo.Route;
import com.zeyuli.service.MapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 高德地图服务实现
 *
 * @author 李泽聿
 * @since 2025-11-01
 */
@Service
@Slf4j
public class AmapMapServiceImpl implements MapService {

    private static final String KEY = "your_amap_api_key"; // 需要替换为实际的高德地图API密钥
    private static final String BASE_URL = "https://restapi.amap.com/v3";
    private static final String DIRECTION_URL = "https://restapi.amap.com/v3/direction";
    private static final String WEATHER_URL = "https://restapi.amap.com/v3/weather/weatherInfo";
    private static final String POI_DETAIL_URL = "https://restapi.amap.com/v3/place/detail";
    
    // 获取API密钥的方法，便于未来实现密钥管理
    private String getKey() {
        return KEY;
    }

    @Override
    public Point getMapCenter(String location) {
        try {
            // 地理编码API：将地址转换为坐标
            String url = BASE_URL + "/geocode/geo?address=" + URLEncoder.encode(location, "UTF-8") + "&key=" + getKey();
            String result = httpGet(url);
            // 解析JSON响应，这里简化处理，实际需要使用JSON解析库
            if (result.contains("location")) {
                String locationStr = result.substring(result.indexOf("location") + 10);
                locationStr = locationStr.substring(0, locationStr.indexOf(","));
                String[] coords = locationStr.split(",");
                if (coords.length == 2) {
                    Point point = new Point();
                    point.setLng(Double.parseDouble(coords[0]));
                    point.setLat(Double.parseDouble(coords[1]));
                    return point;
                }
            }
        } catch (Exception e) {
            log.error("获取地图中心点失败: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public List<Location> searchLocations(String query, String region) {
        List<Location> locations = new ArrayList<>();
        try {
            // 搜索POI
            String url = BASE_URL + "/place/text?keywords=" + URLEncoder.encode(query, "UTF-8") + "&region=" + 
                         URLEncoder.encode(region, "UTF-8") + "&key=" + getKey();
            String result = httpGet(url);
            // 解析JSON响应，简化处理
            log.info("搜索位置结果: {}", result);
            // 实际实现需要解析JSON并构建Location对象
        } catch (Exception e) {
            log.error("搜索位置失败: {}", e.getMessage());
        }
        return locations;
    }

    @Override
    public Route getRoute(String origin, String destination, String mode) {
        Route route = new Route();
        try {
            // 根据交通方式选择不同的API
            String type = "driving"; // 默认驾车
            if (mode.equalsIgnoreCase("walking")) {
                type = "walking";
            } else if (mode.equalsIgnoreCase("transit")) {
                type = "transit/integrated";
            } else if (mode.equalsIgnoreCase("bicycling")) {
                type = "bicycling";
            } else if (mode.equalsIgnoreCase("taxi")) {
                type = "driving"; // 出租车使用驾车路线，但费用计算不同
            }

            String url = DIRECTION_URL + "/" + type + "?origin=" + origin + "&destination=" + destination + "&key=" + getKey();
            String result = httpGet(url);
            
            // 解析响应，设置路线信息
            route.setMode(mode);
            route.setDistance(5000); // 示例数据
            route.setDuration(3600); // 示例数据
            route.setEstimatedCost(calculateCost(mode, 5000, 3600)); // 计算预估费用
            route.setStartName("起点"); // 示例数据
            route.setEndName("终点"); // 示例数据
            
            // 实际实现需要解析JSON并构建完整的Route对象
        } catch (Exception e) {
            log.error("获取路线失败: {}", e.getMessage());
        }
        return route;
    }

    @Override
    public List<POI> getSurroundingPOIs(String location, String radius, String types) {
        List<POI> pois = new ArrayList<>();
        try {
            // 周边搜索API
            String url = BASE_URL + "/place/around?location=" + location + "&radius=" + radius + 
                         "&types=" + types + "&key=" + getKey();
            String result = httpGet(url);
            // 解析JSON响应，简化处理
            log.info("周边POI搜索结果: {}", result);
            // 实际实现需要解析JSON并构建POI对象
        } catch (Exception e) {
            log.error("获取周边POI失败: {}", e.getMessage());
        }
        return pois;
    }

    @Override
    public String getMapScreenshot(String center, String zoom, String width, String height) {
        try {
            // 静态地图API
            String url = BASE_URL + "/staticmap?location=" + center + "&zoom=" + zoom + "&size=" + width + "," + height + "&key=" + getKey();
            // 返回图片URL
            return url;
        } catch (Exception e) {
            log.error("获取地图截图失败: {}", e.getMessage());
        }
        return "";
    }

    @Override
    public String getWeatherInfo(String cityCode) {
        try {
            String url = WEATHER_URL + "?city=" + cityCode + "&key=" + getKey();
            return httpGet(url);
        } catch (Exception e) {
            log.error("获取天气信息失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 计算交通费用
     * @param mode 交通方式
     * @param distance 距离（米）
     * @param duration 时长（秒）
     * @return 预估费用
     */
    private double calculateCost(String mode, double distance, double duration) {
        switch (mode.toLowerCase()) {
            case "driving":
                // 驾车费用估算：0.8元/公里
                return (distance / 1000) * 0.8;
            case "transit":
                // 公共交通费用估算：固定费用+距离费用
                return 3 + (distance / 10000) * 2;
            case "walking":
            case "bicycling":
                return 0;
            default:
                return 0;
        }
    }

    /**
     * 发送HTTP GET请求
     * @param urlString URL地址
     * @return 响应内容
     */
    private String httpGet(String urlString) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } finally {
            conn.disconnect();
        }
        return result.toString();
    }

    @Override
    public String getAddressByLocation(double lat, double lng) {
        try {
            String url = BASE_URL + "/geocode/regeo?location=" + lng + "," + lat + "&key=" + getKey();
            return httpGet(url);
        } catch (Exception e) {
            log.error("获取地址信息失败: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public double getDistance(String origin, String destination) {
        try {
            String url = BASE_URL + "/distance?origins=" + origin + "&destination=" + destination + "&type=1&key=" + getKey();
            String result = httpGet(url);
            // 解析JSON响应，简化处理
            log.info("距离计算结果: {}", result);
            return 0; // 实际实现需要解析结果
        } catch (Exception e) {
            log.error("获取距离失败: {}", e.getMessage());
            return -1;
        }
    }
    
    @Override
    public Map<String, Object> getPOIDetails(String poiId) {
        Map<String, Object> details = new HashMap<>();
        try {
            String url = POI_DETAIL_URL + "?id=" + poiId + "&key=" + getKey();
            String result = httpGet(url);
            log.info("POI详情结果: {}", result);
            // 实际实现需要解析JSON并填充details
            details.put("id", poiId);
            details.put("price", 0); // 默认价格，实际应从API获取
            details.put("rating", 0); // 默认评分
            details.put("openingHours", "全天开放"); // 默认开放时间
        } catch (Exception e) {
            log.error("获取POI详情失败: {}", e.getMessage());
        }
        return details;
    }
    
    @Override
    public List<POI> getAttractionsByCity(String city, int page, int pageSize) {
        List<POI> attractions = new ArrayList<>();
        try {
            // 搜索城市的景点，types参数设置为景点类型
            String url = BASE_URL + "/place/text?keywords=景点&types=110000&city=" + 
                         URLEncoder.encode(city, "UTF-8") + "&offset=" + pageSize + "&page=" + 
                         page + "&key=" + getKey();
            String result = httpGet(url);
            log.info("城市景点搜索结果: {}", result);
            // 实际实现需要解析JSON并构建POI对象列表
        } catch (Exception e) {
            log.error("获取城市景点列表失败: {}", e.getMessage());
        }
        return attractions;
    }
}