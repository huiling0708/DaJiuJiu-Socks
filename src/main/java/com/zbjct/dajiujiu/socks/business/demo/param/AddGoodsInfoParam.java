package com.zbjct.dajiujiu.socks.business.demo.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import javax.validation.constraints.Positive;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.zbjct.dajiujiu.socks.basics.validate.Unique;
import com.zbjct.dajiujiu.socks.basics.validate.param.ValidateType;

@Data
@ApiModel(description = "新增商品信息参数")
public class AddGoodsInfoParam {

    @NotNull
    @ApiModelProperty(value = "模板id", required = true)
    private Long templateId;

    @NotBlank
    @ApiModelProperty(value = "名称", required = true)
    private String goodsName;

    @Unique(value = ValidateType.GOODS_CODE, inCompany = true)
    @ApiModelProperty(value = "代码", required = true)
    private String goodsCode;

    @NotBlank
    @ApiModelProperty(value = "描述", required = true)
    private String goodsDescribe;

    @NotNull
    @ApiModelProperty(value = "价格", required = true)
    private BigDecimal goodsPrice;

    @Positive
    @ApiModelProperty(value = "数量", required = true)
    private int goodsAmount;


}
