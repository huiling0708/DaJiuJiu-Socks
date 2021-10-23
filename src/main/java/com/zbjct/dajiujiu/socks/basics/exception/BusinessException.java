package com.zbjct.dajiujiu.socks.basics.exception;

import com.zbjct.dajiujiu.socks.basics.constant.ResultCode;

/**
 * 业务异常
 */
public class BusinessException extends PlatformException {

    public BusinessException(String message) {
        super(ResultCode.B00001, message);
    }

    public BusinessException(String message, String... values) {
        super(ResultCode.B00001, message, values);
    }

}
