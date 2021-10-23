package com.zbjct.dajiujiu.socks.basics.define.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel(description = "分页查询排序公共参数")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderParam implements Serializable {
    private static final long serialVersionUID = 5297594041134976385L;


    @ApiModelProperty(value = "排序规则 默认 false 可输入 true", allowableValues = "true,false")
    private boolean isAsc;
    @ApiModelProperty(value = "属性名称 指定排序所需要用到的字段名称")
    private String property;

    public static OrderParam asc(String property) {
        return new OrderParam(true, property);
    }

    public static OrderParam desc(String property) {
        return new OrderParam(false, property);
    }

}
