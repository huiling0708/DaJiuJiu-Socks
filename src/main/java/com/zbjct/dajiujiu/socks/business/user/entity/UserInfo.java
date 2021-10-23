package com.zbjct.dajiujiu.socks.business.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zbjct.dajiujiu.socks.basics.define.entity.DateEntity;
import com.zbjct.dajiujiu.socks.basics.dict.Enable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user_info")
@ApiModel(description = "用户信息")
public class UserInfo extends DateEntity<UserInfo> {


    private static final long serialVersionUID = -4386570739335275815L;
    @ApiModelProperty("用户Id")
    @Id
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @GeneratedValue(generator = "system-uuid")
    @Column(length = 32)
    private String userId;

    @ApiModelProperty("手机号")
    @Column(length = 11, unique = true, nullable = false)
    private String phone;

    @ApiModelProperty("用户名称")
    @Column(length = 128,nullable = false)
    private String name;

    @ApiModelProperty("公司Id")
    @Column(length = 32,nullable = false)
    private String companyId;

    @JsonIgnore
    @ApiModelProperty("密码")
    @Column(length = 64, nullable = false)
    private String password;

    @JsonIgnore
    @ApiModelProperty("盐")
    @Column(length = 32, nullable = false)
    private String salt;

    @ApiModelProperty("用户状态")
    @Column(length = 32, nullable = false)
    @Enumerated(EnumType.STRING)
    private Enable state;

    @JsonIgnore
    public String getCredentialsSalt() {
        return this.phone + this.salt;
    }
}
