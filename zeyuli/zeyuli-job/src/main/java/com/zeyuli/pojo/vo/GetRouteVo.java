package com.zeyuli.pojo.vo;


import lombok.Data;

/**
 * 获取路径规划实体类
 *
 * @author 李泽聿
 * @since 2025-12-10 19:36
 */
@Data
public class GetRouteVo {
    private GetRouteData data;
    private int errcode;
    private String errdetail;
    private String errmsg;
    private String ext;

}
