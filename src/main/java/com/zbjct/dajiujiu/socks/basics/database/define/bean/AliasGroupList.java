package com.zbjct.dajiujiu.socks.basics.database.define.bean;

import com.zbjct.dajiujiu.socks.basics.database.define.IAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 分组列表
 */
@Getter
@AllArgsConstructor
public class AliasGroupList implements IAlias {

    private int aliasIndex;//别名序号
    private String propertyName;//属性名称
}
