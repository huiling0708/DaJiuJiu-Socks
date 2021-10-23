package com.zbjct.dajiujiu.socks.basics.query;

import java.lang.annotation.*;


/**
 * 排序
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QuerySort {

    String value() ;//属性名称

    Direction direction() default Direction.DESC;//排序方向 默认 DESC

    enum Direction {
        ASC, DESC
    }

}
