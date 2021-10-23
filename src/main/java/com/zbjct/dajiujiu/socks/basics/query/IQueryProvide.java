package com.zbjct.dajiujiu.socks.basics.query;


import com.zbjct.dajiujiu.socks.basics.database.define.IEntity;
import com.zbjct.dajiujiu.socks.basics.database.define.IVo;
import com.zbjct.dajiujiu.socks.basics.database.impl.JpaWrapper;

/**
 * 查询接口附属接口 用于增加自定义查询条件或增加连表查询
 *
 * @param <E> 实体类 查询实体
 * @param <R> vo类 返回实体
 */
public interface IQueryProvide<E extends IEntity, R extends IVo> extends IVo {

    /**
     * 其他条件处理
     *
     * @param queryGroup   分组
     * @param queryWrapper 该查询用到的wrapper
     * @param param        查询参数
     */
    void otherConditionHandle(String queryGroup, JpaWrapper<E> queryWrapper, R param);

}
