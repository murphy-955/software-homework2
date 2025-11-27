package com.zeyuli.service.impl;


import com.zeyuli.pojo.bo.Location;
import com.zeyuli.pojo.bo.POI;
import com.zeyuli.pojo.bo.Point;
import com.zeyuli.pojo.bo.Route;
import com.zeyuli.service.MapService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 百度地图的实现
 *
 * @author 李泽聿
 * @since 2025-11-01 10:53
 */

public class BaiduMapServiceImpl implements MapService {
    @Override
    public Point getMapCenter(String location) {
        return null;
    }

    @Override
    public List<Location> searchLocations(String query, String region) {
        return List.of();
    }

    @Override
    public Route getRoute(String origin, String destination, String mode) {
        return null;
    }

    @Override
    public List<POI> getSurroundingPOIs(String location, String radius, String types) {
        return List.of();
    }

    @Override
    public String getMapScreenshot(String center, String zoom, String width, String height) {
        return "";
    }

    @Override
    public String getWeatherInfo(String cityCode) {
        return "";
    }

    @Override
    public String getAddressByLocation(double lat, double lng) {
        return "";
    }

    @Override
    public double getDistance(String origin, String destination) {
        return 0;
    }

    @Override
    public Map<String, Object> getPOIDetails(String poiId) {
        return new HashMap<>();
    }

    @Override
    public List<POI> getAttractionsByCity(String city, int page, int pageSize) {
        return List.of();
    }
}
