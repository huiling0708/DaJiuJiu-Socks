package com.zbjct.dajiujiu.socks.basics.constant;

import lombok.Getter;

/**
 * 结果代码
 */
@Getter
public enum ResultCode {

    S("成功", "操作成功!"),
    E("失败"),
    E99999("系统正忙，请稍后再试"),
    //用户相关
    U00000("Token异常", "登陆失败，请重新登陆"),
    U00001("未登陆"),
    U10000("无权限操作"),
    U90001("密码错误", "用户名或密码错误"),
    U90002("不存在的账户", "用户名或密码错误"),
    U90003("账户异常"),
    //SQL异常
    S00001("SQL错误", "系统错误，请稍后再试"),
    //业务异常
    B00001("业务错误"),
    //参数异常
    C00000("参数为空"),
    C00001("参数错误"),
    //数据无效
    D00000("数据无效"),
    //反射异常
    R00000("反射异常", "系统错误，请稍后再试");

    private String describe;
    private String message;


    ResultCode(String describe) {
        this.describe = describe;
        this.message = describe;
    }

    ResultCode(String describe, String message) {
        this.describe = describe;
        this.message = message;
    }

}
