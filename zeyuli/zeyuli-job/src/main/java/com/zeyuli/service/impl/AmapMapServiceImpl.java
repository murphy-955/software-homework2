package com.zeyuli.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeyuli.pojo.bo.*;
import com.zeyuli.pojo.vo.GaodeAddress;
import com.zeyuli.pojo.vo.Geocodes;
import com.zeyuli.pojo.vo.POIAddress;
import com.zeyuli.pojo.vo.Pois;
import com.zeyuli.service.MapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 高德地图服务实现
 *
 * @author 李泽聿
 * @since 2025-11-01
 */
@Service("amapMapService")
@Slf4j
public class AmapMapServiceImpl implements MapService {

    private static final String KEY = "0f874a8f530e4c8b18ed3b197e682be6"; // 需要替换为实际的高德地图API密钥
    private static final String BASE_URL = "https://restapi.amap.com/v3";
    private static final String DIRECTION_URL = "https://restapi.amap.com/v3/direction";
    private static final String WEATHER_URL = "https://restapi.amap.com/v3/weather/weatherInfo";
    private static final String POI_DETAIL_URL = "https://restapi.amap.com/v3/place/detail";
    
    // 获取API密钥的方法，便于未来实现密钥管理
    private String getKey() {
        return KEY;
    }

    /**
     * 高德返回结果：
     * <pre>
     *     {@code
     *     {
     *   "status" : "1",
     *   "info" : "OK",
     *   "infocode" : "10000",
     *   "count" : "1",
     *   "geocodes" : [ {
     *     "formatted_address" : "江苏省南京市",
     *     "country" : "中国",
     *     "province" : "江苏省",
     *     "citycode" : "025",
     *     "city" : "南京市",
     *     "district" : [ ],
     *     "township" : [ ],
     *     "neighborhood" : {
     *       "name" : [ ],
     *       "type" : [ ]
     *     },
     *     "building" : {
     *       "name" : [ ],
     *       "type" : [ ]
     *     },
     *     "adcode" : "320100",
     *     "street" : [ ],
     *     "number" : [ ],
     *     "location" : "118.796624,32.059344",
     *     "level" : "市"
     *   } ]
     * }}
     * </pre>
     *
     * @author : 李泽聿
     * @since : 2025-12-08 08:20
     * @param location 地址
     * @return : com.zeyuli.pojo.bo.Point
     */
    @Override
    public Point getMapCenter(String location) {
        try {
            // 地理编码API：将地址转换为坐标
            String url = BASE_URL + "/geocode/geo?address=" + URLEncoder.encode(location, StandardCharsets.UTF_8) + "&key=" + getKey();
            String result = httpGet(url);
            if (result.contains("location")) {
                ObjectMapper mapper =new ObjectMapper();
                GaodeAddress gaodeAddress = mapper.readValue(result, GaodeAddress.class);
                // 获取location
                Geocodes geocodes = gaodeAddress.getGeocodes().getFirst();
                // 解析location
                return geocodes.analyzeLatAndLon();
            }
        } catch (Exception e) {
            log.error("获取地图中心点失败: {}", e.getMessage());
        }
        return null;
    }

    public String getCityCode(String cityName) {
        try {
            // 城市编码API：将城市名称转换为城市编码
            String url = BASE_URL + "/geocode/geo?address=" + URLEncoder.encode(cityName, StandardCharsets.UTF_8) + "&key=" + getKey();
            String result = httpGet(url);
            if (result.contains("location")) {
                ObjectMapper mapper =new ObjectMapper();
                GaodeAddress gaodeAddress = mapper.readValue(result, GaodeAddress.class);
                // 获取location
                Geocodes geocodes = gaodeAddress.getGeocodes().getFirst();
                // 解析location
                return geocodes.getCitycode();
            }
        } catch (Exception e) {
            log.error("获取城市编码失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     *
     * @author : 李泽聿
     * @since : 2025-12-08 08:56
     * @param query 搜索关键词
     * @param region 区域
     * @return : java.util.List<com.zeyuli.pojo.bo.Location>
     */
    @Override
    public List<Location> searchLocations(String query, String region) {
        List<Location> locations = new ArrayList<>();
        try {
            // 搜索POI
            String url = BASE_URL + "/place/text?keywords=" + URLEncoder.encode(query, StandardCharsets.UTF_8) + "&region=" +
                         URLEncoder.encode(region, StandardCharsets.UTF_8) + "&key=" + getKey();
            String result = httpGet(url);
            ObjectMapper mapper = new ObjectMapper();
            POIAddress poiAddress = mapper.readValue(result, POIAddress.class);

            String cityCode = getCityCode(region);
            List<Pois> pois = poiAddress.getPois();
            for (Pois poi : pois) {
                Location location = new Location();
                // 解析名称
                location.setName(poi.getName());
                // 分别设置经纬度
                String[] latAndLon = poi.getLocation().split(",");
                location.setLat(Double.parseDouble(latAndLon[0]));
                location.setLng(Double.parseDouble(latAndLon[1]));
                // 设置地址
                location.setAddress(poi.getAddress());
                // 设置类型
                location.setType(poi.getType());
                // 设置城市编码
                location.setCityCode(cityCode);
                locations.add(location);
            }
        } catch (Exception e) {
            log.error("搜索位置失败: {}", e.getMessage());
        }
        return locations;
    }

    /**
     * 规划路线
     *
     * @author : 李泽聿
     * @since : 2025-12-08 09:51
     * @param origin 起点
     * @param destination 终点
     * @param mode 交通方式
     * @return : com.zeyuli.pojo.bo.Route
     */
    @Override
    // todo 待测试
    public Route getRoute(String origin, String destination, String mode) {
        Route route = new Route();
        try {
            // 1. 构造 URL
            String type = switch (mode.toLowerCase()) {
                case "walking" -> "walking";
                case "transit" -> "transit/integrated";
                case "bicycling" -> "bicycling";
                default -> "driving";
            };
            String url = DIRECTION_URL + "/" + type +
                    "?origin=" + origin + "&destination=" + destination + "&key=" + getKey();
            String json = httpGet(url);

            // 2. 解析
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            if (!"1".equals(root.get("status").asText())) {
                log.warn("路径规划失败：{}", root.get("info").asText());
                return route;
            }

            // 3. 取第一条路径
            JsonNode path = root.at("/route/paths/0");
            if (path == null || path.isMissingNode()) return route;

            double distance = path.get("distance").asDouble();          // 米
            double duration = path.get("duration").asDouble();          // 秒
            String polyline = path.get("polyline").asText();            // 轨迹
            String startName = path.at("/origin/name").asText("起点");
            String endName = path.at("/destination/name").asText("终点");

            // 4. 填充 Route
            route.setMode(mode);
            route.setDistance(distance);
            route.setDuration(duration);
            route.setPolyline(polyline);
            route.setStartName(startName);
            route.setEndName(endName);
            route.setEstimatedCost(calculateCost(mode, distance, duration));

            // 5. 解析 steps
            List<RouteStep> steps = new ArrayList<>();
            if (type.equals("walking") || type.equals("bicycling")) {
                // 步行/骑行 steps 是已拼好的字符串，按“；”切
                String stepStr = path.get("steps").asText("");
                String[] arr = stepStr.split("；");
                for (String s : arr) {
                    RouteStep step = new RouteStep();
                    step.setInstruction(s);
                    step.setMode(mode);
                    steps.add(step);
                }
            } else {
                // driving/transit 步骤较多，可再按 /steps 数组细解析，这里先简化
                JsonNode stepArr = path.get("steps");
                if (stepArr != null && stepArr.isArray()) {
                    for (JsonNode s : stepArr) {
                        RouteStep step = new RouteStep();
                        step.setInstruction(s.get("instruction").asText(""));
                        step.setDistance(s.get("distance").asDouble(0));
                        step.setDuration(s.get("duration").asDouble(0));
                        step.setMode(mode);
                        steps.add(step);
                    }
                }
            }
            route.setSteps(steps);

        } catch (Exception e) {
            log.error("获取路线失败", e);
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