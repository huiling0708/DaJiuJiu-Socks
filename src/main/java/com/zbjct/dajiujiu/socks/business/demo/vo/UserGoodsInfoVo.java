package com.zbjct.dajiujiu.socks.business.demo.vo;

import com.zbjct.dajiujiu.socks.basics.database.define.IVo;
import com.zbjct.dajiujiu.socks.basics.query.QueryProvide;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.zbjct.dajiujiu.socks.business.demo.entity.GoodsInfo;
import java.math.BigDecimal;
import com.zbjct.dajiujiu.socks.basics.database.define.constant.SqlExpression;
import com.zbjct.dajiujiu.socks.basics.query.QueryField;
import com.zbjct.dajiujiu.socks.basics.dict.GoodsState;

@Data
@ApiModel(description = "用户商品浏览")
@QueryProvide(value = "用户商品浏览分页查询", entityType = GoodsInfo.class,needLogin = false)
public class UserGoodsInfoVo implements IVo {

    private static final long serialVersionUID = 4321360132084182704L;

    public UserGoodsInfoVo(GoodsInfo goodsInfo) {
        this.copyProperty(goodsInfo);
    }

    @ApiModelProperty("商品id")
    private long goodsId;

    @ApiModelProperty("公司id")
    private String companyId;

    @ApiModelProperty("模板id")
    private long templateId;

    @QueryField(condition = SqlExpression.LIKE)
    @ApiModelProperty("名称")
    private String goodsName;

    @ApiModelProperty("代码")
    private String goodsCode;

    @ApiModelProperty("描述")
    private String goodsDescribe;

    @QueryField(condition = {SqlExpression.GREATER_OR_EQUAL,SqlExpression.LESS_OR_EQUAL})
    @ApiModelProperty("价格")
    private BigDecimal goodsPrice;

    @ApiModelProperty("数量")
    private int goodsAmount;

    @ApiModelProperty("状态")
    private GoodsState goodsState;

}
