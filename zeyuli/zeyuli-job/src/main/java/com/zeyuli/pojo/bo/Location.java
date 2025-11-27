package com.zeyuli.pojo.bo;

import lombok.Data;

/**
 * 位置信息
 *
 * @author 李泽聿
 * @since 2025-11-01 10:45
 */
@Data
public class Location {
    private String name;
    private double lat;
    private double lng;
    private String address;
    private String type;
    private String cityCode;
}
