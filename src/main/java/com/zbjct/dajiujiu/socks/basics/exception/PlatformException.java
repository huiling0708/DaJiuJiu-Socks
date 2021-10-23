package com.zbjct.dajiujiu.socks.basics.exception;


import com.zbjct.dajiujiu.socks.basics.constant.ResultCode;

/**
 * 平台异常
 */
public class PlatformException extends RuntimeException {

    private ResultCode resultCode;

    public PlatformException(String message) {
        super(message);
        this.resultCode = ResultCode.E;
    }

    public PlatformException(ResultCode resultCode) {
        this(resultCode, resultCode.getMessage());
    }

    public PlatformException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    public PlatformException(ResultCode resultCode, String message, String... args) {
        super(args != null && args.length > 0 ? String.format(message, args) : message);
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}
