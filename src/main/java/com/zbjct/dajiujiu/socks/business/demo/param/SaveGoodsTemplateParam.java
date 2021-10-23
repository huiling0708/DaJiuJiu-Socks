package com.zbjct.dajiujiu.socks.business.demo.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description = "保存商品模板参数")
public class SaveGoodsTemplateParam {

    @ApiModelProperty(value = "模板id")
    private long templateId;

    @NotBlank
    @ApiModelProperty(value = "模板名称", required = true)
    private String templateName;

    @NotBlank
    @ApiModelProperty(value = "模板资源", required = true)
    private String templateResources;


}
