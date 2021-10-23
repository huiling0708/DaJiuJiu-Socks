package com.zbjct.dajiujiu.socks.basics.define.vo;

import com.zbjct.dajiujiu.socks.basics.constant.ResultCode;
import com.zbjct.dajiujiu.socks.basics.exception.PlatformException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;


@ToString
@Getter
@ApiModel(description = "公共返回")
public class ResultVo<T> {

    @ApiModelProperty(value = "结果代码 S时操作成功，非S均为失败，可以根据具体的错误代码做相应处理", example = "S")
    private String code;
    @ApiModelProperty(value = "结果消息 当结果代码非S时，把消息中的内容展示", example = "操作成功")
    private String message;
    @ApiModelProperty(value = "结果数据")
    private T data;

    public ResultVo(ResultCode resultCode) {
        this(resultCode.name(), resultCode.getMessage());
    }

    public ResultVo(String code, String message) {
        this(code, message, null);
    }

    public ResultVo(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResultVo success() {
        return success(ResultCode.S.getMessage());
    }

    public static ResultVo success(String message) {
        return success(message, null);
    }

    public static <T> ResultVo<T> success(T data) {
        return success(ResultCode.S.getMessage(), data);
    }

    public static <T> ResultVo<T> success(String message, T data) {
        return new ResultVo(ResultCode.S.name(), message, data);
    }

    public static ResultVo error(PlatformException exception) {
        return error(exception.getResultCode(), exception.getMessage());
    }

    public static ResultVo error(ResultCode resultCode) {
        return error(resultCode.name(), resultCode.getMessage());
    }

    public static ResultVo error(ResultCode resultCode, String message) {
        return error(resultCode.name(), message);
    }

    public static ResultVo error(String code, String message) {
        return new ResultVo(code, message);
    }

    public static ResultVo error(String message) {
        return new ResultVo("E", message);
    }

    public void setData(T data) {
        this.data = data;
    }

}
