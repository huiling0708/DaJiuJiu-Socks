package com.zbjct.dajiujiu.socks.basics.database.define;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zbjct.dajiujiu.socks.basics.database.impl.JpaWrapper;
import com.zbjct.dajiujiu.socks.basics.dict.base.IDict;
import com.zbjct.dajiujiu.socks.basics.exception.BusinessException;
import com.zbjct.dajiujiu.socks.basics.utils.ClassUtils;

/**
 * 实体接口
 * 数据实体类实现该接口以完成对数据库的交互
 *
 * @param <T>
 */
public interface IEntity<T extends IEntity> extends IVo {
    /**
     * 以函数形式传入指定实体的具体字段，返回该实体
     *
     * @param field
     * @param <NT>
     * @return
     */
    static <NT extends IEntity> IEntity field(PropertyFunc<NT, ?> field) {
        return new IEntity() {
            @Override
            public PropertyFunc<?, ?> getField() {
                return field;
            }
        };
    }

    /**
     * 创建一个 JPA 封装
     *
     * @return
     */
    default JpaWrapper<T> createWrapper() {
        JpaWrapper<T> entityWrapper = new JpaWrapper<>((Class<T>) this.getClass());
        entityWrapper.setEntityObject((T) this);
        return entityWrapper;
    }

    /**
     * 检查当前对象中指定字段是否等于指定值 如果不等于则返回异常
     *
     * @param property 属性字段
     * @param value    值
     * @param message  异常提示消息
     * @return
     */
    default T checkEquals(PropertyFunc<T, ?> property, Object value, String message) {
        if (!value.equals(property.apply((T) this))) {
            throw new BusinessException(message);
        }
        return (T) this;
    }

    /**
     * 检查当前对象中指定字段是否等于指定值 如果不等于则返回异常
     * @param property
     * @param value
     * @return
     */
    default T checkEquals(PropertyFunc<T, ?> property, IDict value) {
        if (!value.equals(property.apply((T) this))) {
            throw new BusinessException("非[%s]中的[%s]", value.getDescribe(),
                    ClassUtils.getClassDescribe(this.getClass()));
        }
        return (T) this;
    }

    /**
     * 检查当前对象中指定字段是否不等于指定值 如果等于则返回异常
     * @param property
     * @param value
     * @param message
     * @return
     */
    default T checkNotEquals(PropertyFunc<T, ?> property, Object value, String message) {
        if (value.equals(property.apply((T) this))) {
            throw new BusinessException(message);
        }
        return (T) this;
    }

    /**
     * 检查当前对象中指定字段是否不等于指定值 如果等于则返回异常
     * @param property
     * @param value
     * @return
     */
    default T checkNotEquals(PropertyFunc<T, ?> property, IDict value) {
        if (value.equals(property.apply((T) this))) {
            throw new BusinessException("[%s]中的[%s]", value.getDescribe(),
                    ClassUtils.getClassDescribe(this.getClass()));
        }
        return (T) this;
    }

    /**
     * 获取实体字段的方法
     *
     * @return
     */
    @JsonIgnore
    default PropertyFunc<?, ?> getField() {
        return null;
    }
}
