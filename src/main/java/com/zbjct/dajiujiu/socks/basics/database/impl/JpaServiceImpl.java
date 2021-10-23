package com.zbjct.dajiujiu.socks.basics.database.impl;

import com.zbjct.dajiujiu.socks.basics.database.JpaService;
import com.zbjct.dajiujiu.socks.basics.database.define.IEntity;
import com.zbjct.dajiujiu.socks.basics.define.vo.PageVo;
import com.zbjct.dajiujiu.socks.basics.exception.BusinessException;
import com.zbjct.dajiujiu.socks.basics.exception.JpaSQLException;
import com.zbjct.dajiujiu.socks.basics.helper.Check;
import com.zbjct.dajiujiu.socks.basics.utils.ClassUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * JPA 服务实现
 */
@Repository
@Slf4j
public class JpaServiceImpl implements JpaService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public <T extends IEntity> T get(JpaWrapper<T> wrapper) {
        return this.get(wrapper, wrapper.getEntityClassType());
    }

    @Override
    public <T extends IEntity, R> R get(JpaWrapper<T> wrapper, Class<R> resultClassType) {
        List<R> resultList = this.list(wrapper, resultClassType);
        if (resultList.size() > 1) {
            throw new JpaSQLException("The Results Are Not Unique");
        }
        return resultList.size() == 1 ? resultList.get(0) : null;
    }

    @Override
    public <T extends IEntity> List<T> list(JpaWrapper<T> wrapper) {
        return this.list(wrapper, wrapper.getEntityClassType());
    }

    @Override
    public <T extends IEntity, R> List<R> list(JpaWrapper<T> wrapper, Class<R> resultClassType) {
        String selectSql = wrapper.getSelectHql();
        TypedQuery<R> query = this.entityManager.createQuery(selectSql, resultClassType);
        wrapper.handleCondition(query);
        return query.getResultList();
    }

    @Override
    public <T extends IEntity> PageVo<T> page(JpaWrapper<T> wrapper) {
        return this.page(wrapper, wrapper.getEntityClassType());
    }

    @Override
    public <T extends IEntity, R> PageVo<R> page(JpaWrapper<T> wrapper, Class<R> resultClassType) {
        Integer limitSize = wrapper.getLimitSize();
        if (limitSize == null) {
            wrapper.limit(PageVo.DEFAULT_PAGE_NUMBER, PageVo.DEFAULT_PAGE_SIZE);
        }
        long count = this.count(wrapper);
        List<R> resultList = count <= 0 ?
                Collections.emptyList() :
                this.list(wrapper, resultClassType);

        PageVo<R> result = new PageVo<>();
        result.setNumber(wrapper.getLimitNumber());
        result.setSize(wrapper.getLimitSize());
        result.setTotalElements(count);
        result.setContent(resultList);
        return result;
    }

    /**
     * 获取单条并加锁（行锁）使用时必须加事物标签
     *
     * @param classType
     * @param primaryKey
     * @param <T>
     * @return
     */
    @Transactional
    @Override
    public <T extends IEntity> T getAndLock(Class<T> classType, Object primaryKey) {
        Check.notNull(primaryKey, "查询主键为空");
        return this.entityManager.find(classType, primaryKey, LockModeType.PESSIMISTIC_WRITE);
    }

    @Override
    public <T extends IEntity> long count(JpaWrapper<T> wrapper) {
        String countSql = wrapper.getCountHql();
        Query query = this.entityManager.createQuery(countSql);
        wrapper.handleCondition(query, false);
        try {
            return (long) query.getSingleResult();
        } catch (NoResultException e) {
            return 0;
        }
    }

    @Override
    public <T extends IEntity> boolean exist(JpaWrapper<T> wrapper) {
        return this.count(wrapper) > 0;
    }

    @Override
    @Transactional
    public <T extends IEntity> int delete(JpaWrapper<T> wrapper) {
        String deleteSql = wrapper.getDeleteHql();
        Query query = this.entityManager.createQuery(deleteSql);
        wrapper.handleCondition(query);
        return query.executeUpdate();
    }

    @Override
    @Transactional
    public <T extends IEntity> int updateValue(JpaWrapper<T> wrapper) {
        String updateSql = wrapper.getUpdateHql();
        Query query = this.entityManager.createQuery(updateSql);
        wrapper.handleCondition(query);
        wrapper.handleUpdateValue(query);
        return query.executeUpdate();
    }

    @Transactional
    @Override
    public <T extends IEntity> T save(T entity) {
        Check.notNull(entity, "保存内容为空");
        if (this.persist(entity)) {
            this.entityManager.persist(entity);
            return entity;
        }
        return this.entityManager.merge(entity);
    }

    @Transactional
    @Override
    public <T extends IEntity> Iterable<T> saveAll(Iterable<T> entityList) {
        Check.notNull(entityList, "批量保存列表为空");
        CallFunction func = () -> {
            try {
                entityManager.flush();
            } catch (Exception e) {
                log.error("批量保存失败", e);
                throw new BusinessException(e.getMessage());
            } finally {
                entityManager.clear();
            }
        };
        List<T> result = new ArrayList();
        Iterator<T> iterator = entityList.iterator();
        //批量只做插入处理
        while (iterator.hasNext()) {
            T t = iterator.next();
            this.entityManager.persist(t);
            result.add(t);
            if (result.size() % 50 == 0) {
                func.call();
            }
        }
        if (result.size() % 50 != 0) {
            func.call();
        }
        return entityList;
    }

    /**
     * 主键值是否存在值 不存在时直接调用 entityManager persist方法
     *
     * @param entity
     * @param <T>
     * @return
     */
    private <T extends IEntity> boolean persist(T entity) {
        List<Field> allColumnFields = ClassUtils.getAllFields(entity.getClass(), Id.class, true);
        Check.notNull(entity, "主键个数为空");
        for (Field field : allColumnFields) {
            Object fieldValue = ClassUtils.getFieldValue(entity, field);
            if (fieldValue == null) {
                return true;
            }
        }
        //存在值 并且是单主键的情况
        // 判断主键是否有生成策略 如果有，则判断传入的主键值是否有效
        if (allColumnFields.size() == 1) {
            Field field = allColumnFields.get(0);
            if (field.isAnnotationPresent(GeneratedValue.class)) {
                Object fieldValue = ClassUtils.getFieldValue(entity, field);
                if ("0".equals(String.valueOf(fieldValue))
                        && (long.class.isAssignableFrom(field.getType())
                        || int.class.isAssignableFrom(field.getType()))) {
                    return false;
                }
                IEntity iEntity = entityManager.find(entity.getClass(), fieldValue);
                if (iEntity == null) {
                    throw new JpaSQLException(
                            String.format("无效的[%s]值[%s]", field.getName(), fieldValue));
                }
            }
        }
        return false;
    }

    @FunctionalInterface
    private interface CallFunction {
        void call();
    }
}
