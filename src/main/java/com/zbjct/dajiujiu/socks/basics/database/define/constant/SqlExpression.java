package com.zbjct.dajiujiu.socks.basics.database.define.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Sql表达式
 */
@AllArgsConstructor
@Getter
public enum SqlExpression {

    EQUALS(" = "),
    NOT_EQUALS(" <> "),
    LIKE(" LIKE ") {
        @Override
        public Object valueHandle(Object value) {
            return "%" + value + "%";
        }
    }, //like %?%
    LIKE_LEFT(" LIKE ") {
        @Override
        public Object valueHandle(Object value) {
            return "%" + value;
        }
    },//like %?
    LIKE_RIGHT(" LIKE ") {
        @Override
        public Object valueHandle(Object value) {
            return value + "%";
        }
    },//like ?%
    GREATER(" > "),
    GREATER_OR_EQUAL(" >= "),
    LESS(" < "),
    LESS_OR_EQUAL(" <= "),
    IN(" IN(", ") "),
    NOT_IN(" NOT IN(", ") "),
    EXISTS(" EXISTS(", ") "),
    NOT_EXISTS(" NOT EXISTS(", ") "),
    IS_NOT_NULL(" IS NOT NULL ", false),
    IS_NULL(" IS NULL ", false);

    private String value;
    private String endValue;
    private boolean needParamValue;//是否需要参数值 当前条件在作为where 条件时，是否需要使用实际的条件

    SqlExpression(String value) {
        this.value = value;
        this.endValue = " ";
        this.needParamValue = true;
    }

    SqlExpression(String value, String endValue) {
        this.value = value;
        this.endValue = endValue;
        this.needParamValue = true;
    }

    SqlExpression(String value, boolean needParamValue) {
        this.value = value;
        this.endValue = " ";
        this.needParamValue = needParamValue;
    }

    /**
     * 实际的查询条件值处理
     *
     * @param value
     * @return
     */
    public Object valueHandle(Object value) {
        return value;
    }
}
