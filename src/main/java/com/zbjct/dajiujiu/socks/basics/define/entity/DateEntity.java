package com.zbjct.dajiujiu.socks.basics.define.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zbjct.dajiujiu.socks.basics.database.define.IEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

/**
 * 时间实体父类
 *
 * @param <T>
 */
@Data
@MappedSuperclass
public abstract class DateEntity<T extends DateEntity> implements IEntity<T> {

    private static final long serialVersionUID = -6518544111015038849L;

    @ApiModelProperty(value = "创建时间", hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(updatable = false)
    private Date createTime;

    @ApiModelProperty(value = "更新时间", hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date updateTime;

    @PrePersist
    public void onCreate() {
        this.setCreateTime(new Date());
        this.setUpdateTime(this.getCreateTime());
    }

    @PreUpdate
    public void onUpdate() {
        this.setUpdateTime(new Date());
    }
}
