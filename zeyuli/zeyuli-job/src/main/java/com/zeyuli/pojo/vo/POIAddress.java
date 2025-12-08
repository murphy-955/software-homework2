package com.zeyuli.pojo.vo;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class POIAddress {

    private Suggestion suggestion;
    private String count;
    private String infocode;
    private List<Pois> pois;
    private String status;
    private String info;
}

/**
 * 搜索建议
 *
 * @author : 李泽聿
 * @since : 2025-12-08 09:01
 */
@Setter
@Getter
class Suggestion {
    private List<String> keywords;
    private List<String> cities;
}


@Getter
@Setter
class Biz_ext {

    private JsonNode cost;
    private JsonNode opentime2;
    private JsonNode level;
    private JsonNode rating;
    private JsonNode open_time;
    private JsonNode ticket_ordering;
}


@Setter
@Getter
class Photos {
    private JsonNode provider;
    private JsonNode title;
    private JsonNode url;
}