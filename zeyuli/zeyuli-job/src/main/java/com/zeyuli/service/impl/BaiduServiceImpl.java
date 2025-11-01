package com.zeyuli.service.impl;


import com.zeyuli.pojo.bo.Location;
import com.zeyuli.pojo.bo.POI;
import com.zeyuli.pojo.bo.Point;
import com.zeyuli.pojo.bo.Route;
import com.zeyuli.service.MapService;

import java.util.List;

/**
 * 百度地图的实现
 *
 * @author 李泽聿
 * @since 2025-11-01 10:53
 */

public class BaiduServiceImpl implements MapService {
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
}
