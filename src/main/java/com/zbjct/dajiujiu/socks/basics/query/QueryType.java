package com.zbjct.dajiujiu.socks.basics.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 查询类型
 */
@AllArgsConstructor
@Getter
public enum QueryType {
    PAGE("分页查询"),
    LIST("列表查询"),
    SINGLE("单条查询"),
    ;
    private String describe;
}
