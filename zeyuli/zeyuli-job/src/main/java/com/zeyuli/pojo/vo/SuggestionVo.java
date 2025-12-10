package com.zeyuli.pojo.vo;


import lombok.Data;

import java.util.List;

/**
 *
 *
 * @author 李泽聿
 * @since 2025-12-10 18:42
 */
@Data
public class SuggestionVo {
    private List<String> keywords;
    private List<String> cities;
}
