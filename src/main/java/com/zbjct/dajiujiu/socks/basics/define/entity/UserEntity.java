package com.zbjct.dajiujiu.socks.basics.define.entity;

import com.zbjct.dajiujiu.socks.basics.helper.SessionHelper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;


/**
 * 用户实体父类
 *
 * @param <T>
 */
@MappedSuperclass
@Data
public abstract class UserEntity<T extends UserEntity> extends DateEntity<T> {
    private static final long serialVersionUID = 6955953995098264210L;

    @ApiModelProperty(value = "创建用户", hidden = true)
    @Column(updatable = false)
    private String createUser;

    @ApiModelProperty(value = "更新用户", hidden = true)
    @Column
    private String updateUser;

    @Override
    public void onCreate() {
        super.onCreate();
        this.setCreateUser(SessionHelper.getUserIdNotCheck());
        this.setUpdateUser(SessionHelper.getUserIdNotCheck());
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.setUpdateUser(SessionHelper.getUserIdNotCheck());
    }
}
