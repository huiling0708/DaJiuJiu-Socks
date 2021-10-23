package com.zbjct.dajiujiu.socks.basics.constant;

/**
 * 常量
 */
public interface Constant {

    //Object 标识
    String OBJECT = "Object";

    //公司ID 字段名称
    String COMPANY_ID_FIELD_NAME="companyId";

    //手机号正则表达式
    String PHONE_REGULAR_EXPRESSION = "^((17[0-9])|(19[0-9])|(16[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";

    //实体包
    String ENTITY_PRE_FIX = "com.zbjct.dajiujiu.socks.business";
    //控制器包
    String CONTROLLER_PRE_FIX = "com.zbjct.dajiujiu.socks.controller";
    //字典包
    String DICT_PRE_FIX = "com.zbjct.dajiujiu.socks.basics.dict";
}
