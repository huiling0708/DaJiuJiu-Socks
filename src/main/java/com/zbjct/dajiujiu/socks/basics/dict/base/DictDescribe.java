package com.zbjct.dajiujiu.socks.basics.dict.base;


import java.lang.annotation.*;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DictDescribe {

    String value();//描述

    String type() default "";//类型，默认为 className
}
