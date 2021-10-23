package com.zbjct.dajiujiu.socks.basics.query;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 查询提供者 为前端提供一个查询方法
 */
@Target(TYPE)
@Retention(RUNTIME)
@Repeatable(QueryProvides.class)
public @interface QueryProvide {

    String value();//查询方法名称

    String queryGroup() default ""; //查询分组

    QueryType[] queryType() default QueryType.PAGE;//支持的查询类型

    QuerySort[] sort() default {@QuerySort("updateTime")};//排序

    boolean needLogin() default true;//需要登陆

    Class<?> entityType() default Object.class;//实体类型 即实体 当返回类型为当前实体类时，不需要额外指定该值

    Class<?> resultType() default Object.class;//返回类型 即视图 当返回类型是当前类时，不需要额外指定该值

    Class<?> controller() default Object.class;//控制器 该方法默认存放在固定分组中，指定后将添加到指定控制器所在分组中
}
