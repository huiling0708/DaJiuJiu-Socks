package com.zbjct.dajiujiu.socks.business.user.entity;

import com.zbjct.dajiujiu.socks.basics.define.entity.DateEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name = "company_info")
@ApiModel(description = "公司信息")
public class CompanyInfo extends DateEntity<CompanyInfo> {

    @ApiModelProperty("公司Id")
    @Id
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @GeneratedValue(generator = "system-uuid")
    @Column(length = 32)
    private String companyId;

    @ApiModelProperty("公司名称")
    @Column(length = 128)
    private String name;

}
