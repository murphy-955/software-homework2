/**
 * Copyright 2025 bejson.com
 */
package com.zeyuli.pojo.vo;

import com.zeyuli.pojo.bo.Point;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 高德地图API返回的地址信息
 *
 * @author : 李泽聿
 * @since : 2025-12-08 08:30
 */
@Setter
@Getter
public class Geocodes {

    private String formatted_address;
    private String country;
    private String province;
    private String citycode;
    private String city;
    private List<String> district;
    private List<String> township;
    private Neighborhood neighborhood;
    private Building building;
    private String adcode;
    private List<String> street;
    private List<String> number;
    private String location;
    private String level;

    /**
     * 解析经纬度字符串为Point对象
     *
     * @author : 李泽聿
     * @since : 2025-12-08 08:43
     * @param location 经纬度字符串，格式为"纬度,经度"
     * @return : Point
     */
    public Point analyzeLatAndLon() {
        String[] latLon = location.split(",");
        double lat = Double.parseDouble(latLon[0]);
        double lon = Double.parseDouble(latLon[1]);
        return new Point(lat, lon);
    }

}