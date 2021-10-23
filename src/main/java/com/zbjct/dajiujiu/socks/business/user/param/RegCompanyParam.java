package com.zbjct.dajiujiu.socks.business.user.param;

import com.zbjct.dajiujiu.socks.basics.constant.Constant;
import com.zbjct.dajiujiu.socks.basics.validate.Unique;
import com.zbjct.dajiujiu.socks.basics.validate.param.ValidateType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@ApiModel(description = "注册公司参数")
public class RegCompanyParam {

    @Unique(ValidateType.USER_PHONE)
    @Pattern(regexp = Constant.PHONE_REGULAR_EXPRESSION, message = "格式不正确")
    @ApiModelProperty(value = "手机号", required = true)
    private String phone;

    @Unique(ValidateType.COMPANY_NAME)
    @ApiModelProperty(value = "公司名称", required = true)
    private String companyName;

    @NotBlank
    @ApiModelProperty(value = "用户名称", required = true)
    private String userName;

    @NotBlank
    @Size(max = 16)
    @ApiModelProperty(value = "密码", required = true)
    private String password;

}
