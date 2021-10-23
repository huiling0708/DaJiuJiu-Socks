package com.zbjct.dajiujiu.socks.basics.validate.param;

import com.zbjct.dajiujiu.socks.basics.validate.Effective;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;


@ApiModel(description = "验证是否有效")
@Data
public class EffectiveParam {

    @Valid
    @Effective(ValidateType.ALL)
    @ApiModelProperty(value = "验证内容", required = true)
    private ValidateContent content;
}
