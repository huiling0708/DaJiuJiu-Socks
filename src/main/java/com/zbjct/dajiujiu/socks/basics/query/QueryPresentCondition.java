package com.zbjct.dajiujiu.socks.basics.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 查询当前条件 从session 中获取
 */
@AllArgsConstructor
@Getter
public enum QueryPresentCondition {
    NONE("无"),//无
    COMPANY("当前所在公司"),//指定当前公司
    USER("当前登录用户"),//指定当前用户
    ;
    private String describe;
}
