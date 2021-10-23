package com.zbjct.dajiujiu.socks.basics.helper;


import com.zbjct.dajiujiu.socks.basics.constant.ResultCode;
import com.zbjct.dajiujiu.socks.basics.exception.PlatformException;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

/**
 * 检查
 */
public abstract class Check {

    /**
     * 参数不为空
     *
     * @param value   值
     * @param message 为空时提示信息
     */
    public static void notNull(String value, String message) {
        if (StringUtils.isBlank(value)) {
            throw new PlatformException(ResultCode.C00000, message);
        }
    }

    /**
     * 参数不为空
     *
     * @param value   值
     * @param message 为空时提示信息
     */
    public static void notNull(Object value, String message) {
        if (value == null) {
            throw new PlatformException(ResultCode.C00000, message);
        }
    }

    /**
     * 数组不为空
     *
     * @param value   值
     * @param message 为空时提示信息
     */
    public static void notNull(Object[] value, String message) {
        if (value == null || value.length == 0) {
            throw new PlatformException(ResultCode.C00000, message);
        }
    }

    /**
     * 集合不为空
     *
     * @param value   值
     * @param message 为空时提示信息
     */
    public static void notNull(Collection<?> value, String message) {
        if (value == null || value.isEmpty()) {
            throw new PlatformException(ResultCode.C00000, message);
        }
    }
}
