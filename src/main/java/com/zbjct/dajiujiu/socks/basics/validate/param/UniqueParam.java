package com.zbjct.dajiujiu.socks.basics.validate.param;

import com.zbjct.dajiujiu.socks.basics.validate.Unique;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel(description = "验证是否唯一")
@Data
public class UniqueParam {

    @Unique(ValidateType.ALL)
    @ApiModelProperty(value = "验证内容", required = true)
    private ValidateContent content;
}
