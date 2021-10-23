package com.zbjct.dajiujiu.socks.basics.database.impl;


import com.zbjct.dajiujiu.socks.basics.database.define.HqlBuilder;
import com.zbjct.dajiujiu.socks.basics.database.define.IEntity;
import com.zbjct.dajiujiu.socks.basics.database.define.PropertyFunc;
import com.zbjct.dajiujiu.socks.basics.database.define.bean.JpaCondition;
import com.zbjct.dajiujiu.socks.basics.database.define.constant.SqlExpression;
import com.zbjct.dajiujiu.socks.basics.database.define.constant.SqlJoinType;
import com.zbjct.dajiujiu.socks.basics.utils.ClassUtils;

import javax.persistence.Query;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 连接封装
 *
 * @param <T>
 * @param <M>
 * @param <J>
 */
public class JoinWrapper<T extends IEntity, M extends IEntity, J extends IEntity> {

    protected HqlBuilder hql;//hql语句
    protected List<JpaCondition> whereValues;//条件
    protected Class<T> entityClassType;//主实体类型
    protected Class<J> joinClassType;//连接实体类型
    protected SqlJoinType joinType;//关联类型
    protected JpaWrapper<M> mainWrapper;//主Wrapper
    protected PropertyFunc<J, ?> joinProperty;//连接属性字段
    protected PropertyFunc<T, ?> myselfProperty;//主实体连接属性
    protected int aliasIndex;//当前wrapper顺序号

    private JoinWrapper() {
    }

    protected JoinWrapper(Class<T> entityClassType, JpaWrapper<M> mainWrapper, Class<J> joinClassType, SqlJoinType joinType, int aliasIndex) {
        this.hql = new HqlBuilder();
        this.whereValues = new ArrayList<>(10);
        this.entityClassType = entityClassType;
        this.joinClassType = joinClassType;
        this.joinType = joinType;
        this.mainWrapper = mainWrapper;
        this.aliasIndex = aliasIndex;
    }

    /**
     * 获取 连接sql
     *
     * @return
     */
    public String getJoinSql() {
        String myselfPropertyValue = HqlBuilder.getAlias(this.aliasIndex, myselfProperty);
        String joinPropertyValue = HqlBuilder.getAlias(this.mainWrapper.getAliasIndex(joinClassType), joinProperty);
        return this.hql.buildJoinHql(
                this.joinType, entityClassType, aliasIndex,
                myselfPropertyValue, joinPropertyValue, this.whereValues).toString();
    }

    /**
     * 在此连接基础上继续直连接
     *
     * @param entityClassType
     * @param <NT>
     * @return
     */
    public <NT extends IEntity> JoinWrapper<NT, M, T> join(Class<NT> entityClassType) {
        return this.join(entityClassType, SqlJoinType.INNER);
    }

    /**
     * 在此连接基础上继续左连接
     *
     * @param entityClassType
     * @param <NT>
     * @return
     */
    public <NT extends IEntity> JoinWrapper<NT, M, T> leftJoin(Class<NT> entityClassType) {
        return this.join(entityClassType, SqlJoinType.LEFT);
    }

    /**
     * 在此连接基础上继续并指定连接类型
     *
     * @param entityClassType
     * @param joinType
     * @param <NT>
     * @return
     */
    public <NT extends IEntity> JoinWrapper<NT, M, T> join(Class<NT> entityClassType, SqlJoinType joinType) {
        int nextAliasIndex = this.mainWrapper.getNextAliasIndex();
        JoinWrapper<NT, M, T> wrapper = new JoinWrapper<>(entityClassType,
                this.mainWrapper, this.entityClassType,
                joinType, this.mainWrapper.getNextAliasIndex());
        this.mainWrapper.addJoin(nextAliasIndex, wrapper);
        return wrapper;
    }

    /**
     * on 语句
     *
     * @param joinProperty
     * @param property
     * @return
     */
    public JoinWrapper<T, M, J> on(PropertyFunc<J, ?> joinProperty, PropertyFunc<T, ?> property) {
        this.joinProperty = joinProperty;
        this.myselfProperty = property;
        return this;
    }

    /**
     * 结束连接
     * 该方法主要用于退出该连接，拿到主Wrapper，方便连续调用
     * @return
     */
    public JpaWrapper<M> endJoin() {
        return this.mainWrapper;
    }

    public JoinWrapper<T, M, J> where(PropertyFunc<T, ?> property, Object value) {
        return this.where(property, value, SqlExpression.EQUALS);
    }

    public JoinWrapper<T, M, J> where(PropertyFunc<T, ?> property, Object value, SqlExpression expression) {
        return this.addCondition(ClassUtils.getFieldName(property), value, expression);
    }

    public JoinWrapper<T, M, J> where(Field field, Object value, SqlExpression expression) {
        return this.addCondition(field.getName(), value, expression);
    }

    private JoinWrapper<T, M, J> addCondition(String propertyName, Object value, SqlExpression expression) {
        this.whereValues.add(new JpaCondition(this.whereValues.size(), propertyName, value, expression, aliasIndex));
        return this;
    }

    protected void handleCondition(Query query) {
        this.whereValues.forEach(w -> {
            query.setParameter(w.getPropertyParam(), w.getValue());
        });
    }

}
