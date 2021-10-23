package com.zbjct.dajiujiu.socks.business.user.param;

import com.zbjct.dajiujiu.socks.basics.constant.Constant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@ApiModel(description = "用户登陆参数")
public class LoginParam {

    @NotBlank
    @Pattern(regexp = Constant.PHONE_REGULAR_EXPRESSION, message = "格式不正确")
    @ApiModelProperty(value = "手机号", required = true)
    private String phone;

    @NotBlank
    @Size(max = 16)
    @ApiModelProperty(value = "密码", required = true)
    private String password;


}
