package com.zbjct.dajiujiu.socks.basics.database.define;


import java.io.Serializable;
import java.util.function.Function;

/**
 * 字段属性函数
 * @param <T>
 * @param <R>
 */
@FunctionalInterface
public interface PropertyFunc<T, R> extends Function<T, R>, Serializable {

}
