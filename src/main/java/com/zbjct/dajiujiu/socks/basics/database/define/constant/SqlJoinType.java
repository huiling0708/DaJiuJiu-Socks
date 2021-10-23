package com.zbjct.dajiujiu.socks.basics.database.define.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * sql 连接类型
 */
@Getter
@AllArgsConstructor
public enum SqlJoinType {

    INNER("INNER JOIN"),
    LEFT("LEFT JOIN"),
    RIGHT("RIGHT JOIN"),
    FULL_OUTER("FULL OUTER JOIN");

    private String value;
}
