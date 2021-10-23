package com.zbjct.dajiujiu.socks.basics.exception;


import com.zbjct.dajiujiu.socks.basics.constant.ResultCode;

/**
 * sql 异常
 */
public class JpaSQLException extends PlatformException {

    public JpaSQLException(String message) {
        super(ResultCode.S00001, message);
    }

}
