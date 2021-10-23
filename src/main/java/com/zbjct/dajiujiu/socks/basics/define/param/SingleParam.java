package com.zbjct.dajiujiu.socks.basics.define.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(description = "单参数入参")
@Data
public class SingleParam<T> implements Serializable {

    private static final long serialVersionUID = 2891170624579999223L;

    @ApiModelProperty(value = "参数值 根据接口指定的参数类型赋值", required = true)
    private T key;
}
