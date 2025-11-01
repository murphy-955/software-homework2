package com.zeyuli.service;

import com.zeyuli.pojo.bo.POI;
import com.zeyuli.pojo.bo.Point;
import com.zeyuli.pojo.bo.Location;
import com.zeyuli.pojo.bo.Route;

import java.util.List;

/**
 * 百度地图服务
 *
 * @author 李泽聿
 * @since 2025-11-01 10:32
 */

public interface MapService {
    Point getMapCenter(String location);
    List<Location> searchLocations(String query, String region);
    Route getRoute(String origin, String destination, String mode);
    List<POI> getSurroundingPOIs(String location, String radius, String types);
    String getMapScreenshot(String center, String zoom, String width, String height);
}
