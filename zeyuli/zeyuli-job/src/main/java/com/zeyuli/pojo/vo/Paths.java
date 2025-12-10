package com.zeyuli.pojo.vo;


import lombok.Data;

import java.util.List;

/**
 *
 *
 * @author 李泽聿
 * @since 2025-12-10 19:38
 */

@Data
public class Paths {
    private int distance;
    private int duration;
    private List<Steps> steps;
}
