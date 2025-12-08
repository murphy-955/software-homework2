package com.zeyuli.pojo.vo;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 根据
 *
 * @author 李泽聿
 * @since 2025-12-08 09:10
 */
@Getter
@Setter
public class Pois {
    private JsonNode parent;
    private String address;
    private List<String> distance;
    private List<String> space_num;
    private String pname;
    private List<String> importance;
    private JsonNode biz_ext;
    private JsonNode biz_type;
    private String cityname;
    private String type;
    private List<Photos> photos;
    private List<String> building;
    private String typecode;
    private String shopinfo;
    private List<String> poiweight;
    private JsonNode childtype;
    private String adname;
    private String name;
    private String location;
    private JsonNode tel;
    private List<String> shopid;
    private String id;
    private List<String> favorite_num;
    private List<String> featured_reviews;
}
