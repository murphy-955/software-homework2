package com.zeyuli.pojo.vo;


import lombok.Data;

/**
 *
 *
 * @author 李泽聿
 * @since 2025-12-10 19:37
 */

@Data
public class Steps {
    private String action;
    private String assistant_action;
    private int distance;
    private int duration;
    private String instruction;
    private String orientation;
    private String polyline;
    private String road;
    private int walk_type;

}