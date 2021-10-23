package com.zbjct.dajiujiu.socks.business.demo.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.zbjct.dajiujiu.socks.basics.dict.GoodsState;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "更新商品状态参数")
public class UpdateGoodsStateParam {

    @NotNull
    @ApiModelProperty(value = "商品id", required = true)
    private Long goodsId;

    @NotNull
    @ApiModelProperty(value = "状态", required = true)
    private GoodsState goodsState;

}
