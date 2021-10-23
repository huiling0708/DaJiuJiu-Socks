package com.zbjct.dajiujiu.socks.basics.query;


import com.zbjct.dajiujiu.socks.basics.database.define.constant.SqlExpression;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 查询参数字段
 */
@Target({FIELD, METHOD})
@Retention(RUNTIME)
@Repeatable(QueryFields.class)
public @interface QueryField {

    String queryGroup() default ""; //查询分组

    boolean mustInput() default false;//是否必输

    SqlExpression[] condition() default {SqlExpression.EQUALS};//条件

    boolean queryNullable() default false;//允许查询空值

    QueryPresentCondition present() default QueryPresentCondition.NONE;//查询当前条件

    String fixedValue() default "";//固定值
}
