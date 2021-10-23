package com.zbjct.dajiujiu.socks.basics.database.define.bean;

import com.zbjct.dajiujiu.socks.basics.database.define.IAlias;
import com.zbjct.dajiujiu.socks.basics.database.define.constant.SqlAggregateType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 聚合函数列表
 */
@Getter
@AllArgsConstructor
public class AliasAggregateList implements IAlias {

    private int aliasIndex;//别名序号
    private String propertyName;//属性名称
    private SqlAggregateType aggregateType;//聚合函数类型
}
