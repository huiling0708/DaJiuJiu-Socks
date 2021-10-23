package com.zbjct.dajiujiu.socks.basics.define.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(description = "分页查询参数")
public class PageParam<T> implements Serializable {
    private static final long serialVersionUID = -9017115272300236039L;

    private final static Integer DEFAULT_SIZE = 20;
    private final static Integer DEFAULT_INDEX = 1;

    @ApiModelProperty(value = "页数 默认第1页")
    private int number;
    @ApiModelProperty(value = "每页显示条数 默认20条")
    private int size;
    @ApiModelProperty(value = "排序集合")
    private List<OrderParam> orders;

    @ApiModelProperty(value = "查询参数", required = true)
    private T queryParams;

    public PageParam() {
        this.size = DEFAULT_SIZE;
        this.number = DEFAULT_INDEX;
    }

    public int getNumber() {
        return number == 0 ? DEFAULT_INDEX : number;
    }

    public int getSize() {
        return size == 0 ? DEFAULT_SIZE : size;
    }
}
