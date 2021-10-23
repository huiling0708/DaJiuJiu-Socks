package com.zbjct.dajiujiu.socks.business.demo.vo;

import com.zbjct.dajiujiu.socks.basics.database.define.IVo;
import com.zbjct.dajiujiu.socks.basics.query.QueryProvide;
import com.zbjct.dajiujiu.socks.basics.query.QueryType;
import com.zbjct.dajiujiu.socks.controller.GoodsManageController;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.zbjct.dajiujiu.socks.business.demo.entity.GoodsInfo;

import java.math.BigDecimal;

import com.zbjct.dajiujiu.socks.basics.query.QueryPresentCondition;
import com.zbjct.dajiujiu.socks.basics.database.define.constant.SqlExpression;
import com.zbjct.dajiujiu.socks.basics.query.QueryField;
import com.zbjct.dajiujiu.socks.basics.dict.GoodsState;

@Data
@ApiModel(description = "商品信息视图模型")
@QueryProvide(value = "商品列表", entityType = GoodsInfo.class,
        queryType = QueryType.LIST, controller = GoodsManageController.class)
@QueryProvide(value = "商品查询", entityType = GoodsInfo.class,
        queryType = QueryType.SINGLE, controller = GoodsManageController.class,queryGroup = "SINGLE")
public class GoodsInfoVo implements IVo {

    private static final long serialVersionUID = 1504123782465151114L;

    public GoodsInfoVo(GoodsInfo goodsInfo) {
        this.copyProperty(goodsInfo);
    }

    @QueryField(mustInput = true,queryGroup = "SINGLE")
    @ApiModelProperty("商品id")
    private long goodsId;

    @QueryField(present = QueryPresentCondition.COMPANY)
    @ApiModelProperty("公司id")
    private String companyId;

    @ApiModelProperty("模板id")
    private long templateId;

    @QueryField(condition = SqlExpression.LIKE)
    @ApiModelProperty("名称")
    private String goodsName;

    @QueryField
    @ApiModelProperty("代码")
    private String goodsCode;

    @ApiModelProperty("描述")
    private String goodsDescribe;

    @ApiModelProperty("价格")
    private BigDecimal goodsPrice;

    @ApiModelProperty("数量")
    private int goodsAmount;

    @QueryField(condition = SqlExpression.IN)
    @ApiModelProperty("状态")
    private GoodsState goodsState;

}
