package com.zbjct.dajiujiu.socks.business.demo.entity;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import com.zbjct.dajiujiu.socks.basics.define.entity.UserEntity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Data
@Entity
@Table(name = "goods_template")
@ApiModel(description = "商品模板")
public class GoodsTemplate extends UserEntity<GoodsTemplate> {

    private static final long serialVersionUID = 6412508261254588356L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("模板id")
    @Column
    private long templateId;

    @ApiModelProperty("公司id")
    @Column(length = 32,nullable = false)
    private String companyId;

    @ApiModelProperty("模板名称")
    @Column(length = 64,nullable = false)
    private String templateName;

    @ApiModelProperty("模板资源")
    @Column(length = 32,nullable = false)
    private String templateResources;

    @ApiModelProperty("商品使用个数")
    @Column(nullable = false)
    private int goodsUsedCount;

}
