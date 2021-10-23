package com.zbjct.dajiujiu.socks.basics.database;


import com.zbjct.dajiujiu.socks.basics.database.define.IEntity;
import com.zbjct.dajiujiu.socks.basics.database.impl.JpaWrapper;
import com.zbjct.dajiujiu.socks.basics.define.vo.PageVo;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * JPA服务
 */
public interface JpaService {

    EntityManager getEntityManager();

    /**
     * 获取单条
     *
     * @return
     */
    <T extends IEntity> T get(JpaWrapper<T> wrapper);

    /**
     * 获取单条
     *
     * @return
     */
    <T extends IEntity, R> R get(JpaWrapper<T> wrapper, Class<R> resultClassType);

    /**
     * 获取列表
     *
     * @return
     */
    <T extends IEntity> List<T> list(JpaWrapper<T> wrapper);

    /**
     * 获取列表
     *
     * @return
     */
    <T extends IEntity, R> List<R> list(JpaWrapper<T> wrapper, Class<R> resultClassType);

    /**
     * 获取分页
     *
     * @return
     */
    <T extends IEntity> PageVo<T> page(JpaWrapper<T> wrapper);

    /**
     * 获取分页
     *
     * @return
     */
    <T extends IEntity, R> PageVo<R> page(JpaWrapper<T> wrapper, Class<R> resultClassType);

    /**
     * 统计
     *
     * @return
     */
    <T extends IEntity> long count(JpaWrapper<T> wrapper);

    /**
     * 是否存在
     *
     * @return
     */
    <T extends IEntity> boolean exist(JpaWrapper<T> wrapper);

    /**
     * 保存
     *
     * @return
     */
    @Transactional
    <T extends IEntity> T save(T entity);

    /**
     * 批量保存
     *
     * @param entityList
     * @param <T>
     * @return
     */
    @Transactional
    <T extends IEntity> Iterable<T> saveAll(Iterable<T> entityList);

    /**
     * 删除
     *
     * @return
     */
    @Transactional
    <T extends IEntity> int delete(JpaWrapper<T> wrapper);

    /**
     * 更新
     *
     * @param wrapper
     * @param <T>
     * @return
     */
    @Transactional
    <T extends IEntity> int updateValue(JpaWrapper<T> wrapper);

    /**
     * 根据主键查询并加锁
     *
     * @param classType
     * @param primaryKey
     * @param <T>
     * @return
     */
    @Transactional
    <T extends IEntity> T getAndLock(Class<T> classType, Object primaryKey);

}
