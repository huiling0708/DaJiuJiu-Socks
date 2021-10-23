package com.zbjct.dajiujiu.socks.basics.database.define.bean;

import com.zbjct.dajiujiu.socks.basics.database.define.IAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 排序列表
 */
@Getter
@AllArgsConstructor
public class AliasOrderList implements IAlias {

    private int aliasIndex;//别名序号
    private String propertyName;//属性名称
    private boolean asc;//是否正序


}
