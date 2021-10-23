package com.zbjct.dajiujiu.socks.basics.validate.param;

import com.zbjct.dajiujiu.socks.basics.dict.LogicType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel(description = "存在验证类型参数")
@Data
public class ValidateContent {

    @NotNull
    @ApiModelProperty(value = "验证类型", required = true)
    private ValidateType validateType;
    @NotNull
    @ApiModelProperty(value = "待验证值", required = true)
    private Object value;
    @ApiModelProperty(value = "在公司中，如验证有效，则验证是否是在当前公司内有效")
    private LogicType inCompany = LogicType.FALSE;
    @ApiModelProperty(value = "自定义错误消息提示")
    private String errorMessage;
}
