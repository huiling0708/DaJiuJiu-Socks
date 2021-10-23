package com.zbjct.dajiujiu.socks.business.demo.entity;

import com.zbjct.dajiujiu.socks.basics.database.define.ILogicDelete;
import com.zbjct.dajiujiu.socks.basics.define.entity.UserEntity;
import com.zbjct.dajiujiu.socks.basics.dict.GoodsState;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "goods_info")
@ApiModel(description = "商品信息")
public class GoodsInfo extends UserEntity<GoodsInfo> implements ILogicDelete {

    private static final long serialVersionUID = 6676240004013323455L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("商品id")
    @Column
    private long goodsId;

    @ApiModelProperty("公司id")
    @Column(length = 32, nullable = false)
    private String companyId;

    @ApiModelProperty("模板id")
    @Column(nullable = false)
    private long templateId;

    @ApiModelProperty("名称")
    @Column(length = 32, nullable = false)
    private String goodsName;

    @ApiModelProperty("代码")
    @Column(length = 16, nullable = false)
    private String goodsCode;

    @ApiModelProperty("描述")
    @Column(length = 500, nullable = false)
    private String goodsDescribe;

    @ApiModelProperty("价格")
    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal goodsPrice;

    @ApiModelProperty("数量")
    @Column(nullable = false)
    private int goodsAmount;

    @ApiModelProperty("状态")
    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private GoodsState goodsState;

    @ApiModelProperty(value = "删除标识", hidden = true)
    @Column(nullable = false)
    private int deleteFlag;
}
