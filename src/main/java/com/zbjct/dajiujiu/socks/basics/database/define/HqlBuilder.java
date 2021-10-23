package com.zbjct.dajiujiu.socks.basics.database.define;

import com.zbjct.dajiujiu.socks.basics.database.define.bean.*;
import com.zbjct.dajiujiu.socks.basics.database.define.constant.SqlExpression;
import com.zbjct.dajiujiu.socks.basics.database.define.constant.SqlJoinType;
import com.zbjct.dajiujiu.socks.basics.database.define.constant.SqlKeyword;
import com.zbjct.dajiujiu.socks.basics.database.impl.JoinWrapper;
import com.zbjct.dajiujiu.socks.basics.exception.JpaSQLException;
import com.zbjct.dajiujiu.socks.basics.utils.ClassUtils;

import java.util.List;
import java.util.Map;

/**
 * Hql生成Builder
 */
public class HqlBuilder {

    private final static String ALIAS_FLAG = "T";
    private final static String SPOT = ".";
    private final static String COLON = ":";
    private final static String COMMA = ",";
    private final static String SPACE = " ";
    private final static String SELECT_HQL = "SELECT T FROM ";
    private final static String DELETE_HQL = "DELETE FROM ";
    private final static String COUNT_HQL = "SELECT COUNT(T) FROM ";
    private final static String GROUP_COUNT_HQL[] = {"SELECT COUNT(DISTINCT ", " ) FROM "};
    private final static String NEW_VIEW_HQL[] = {" NEW ", "(", " ) FROM "};
    private final static String BRACKETS[] = {"(", ")"};

    private StringBuilder hql;

    /**
     * 获取 别名名称
     *
     * @param aliasIndex
     * @param joinProperty
     * @return
     */
    public static String getAlias(int aliasIndex, PropertyFunc<?, ?> joinProperty) {
        //主表别名为T 连接子表别名根据序号依次为 T1、T2、T3……
        StringBuilder alias = new StringBuilder();
        alias.append(ALIAS_FLAG);
        alias.append(aliasIndex == 0 ? "" : aliasIndex);
        alias.append(SPOT);
        alias.append(ClassUtils.getFieldName(joinProperty));
        return alias.toString();
    }

    public HqlBuilder() {
        this.hql = new StringBuilder();
    }

    /**
     * 生成排序
     * order by T.test asc,T1.test2 desc
     * @param orderParams
     * @return
     */
    public HqlBuilder buildOrder(List<AliasOrderList> orderParams) {
        if (orderParams == null || orderParams.size() == 0) {
            return this;
        }
        this.append(SPACE);
        this.append(SqlKeyword.ORDER.name());
        this.space(SqlKeyword.BY.name());
        int index = 0;
        for (AliasOrderList order : orderParams) {
            if (index++ > 0) {
                this.append(COMMA);
            }
            this.aliasSpotName(order);
            this.space(order.isAsc() ? SqlKeyword.ASC.name() : SqlKeyword.DESC.name());
        }
        return this;
    }

    /**
     * 生成分组
     * group by T.test T1.test2
     * @param groupList
     * @return
     */
    public HqlBuilder buildGroup(List<AliasGroupList> groupList) {
        if (groupList == null || groupList.size() == 0) {
            return this;
        }
        this.append(SPACE);
        this.append(SqlKeyword.GROUP.name());
        this.space(SqlKeyword.BY.name());
        int index = 0;
        for (AliasGroupList group : groupList) {
            if (index++ > 0) {
                this.append(COMMA);
            }
            this.aliasSpotName(group);
        }
        return this;
    }

    /**
     * 生成查询语句
     *
     * @param classType    实体类型
     * @param queryView    返回视图类型
     * @param aliasMaps    别名组
     * @param where        条件
     * @param joinWrappers 连接封装
     * @return
     */
    public HqlBuilder buildSelectHql(Class classType, QueryView queryView, Map<Class<? extends IEntity>, Integer> aliasMaps, List<JpaCondition> where, List<JoinWrapper> joinWrappers) {
        if (queryView == null) {
            this.append(SELECT_HQL);
        } else {
            this.queryViewHandle(queryView, aliasMaps);
        }
        this.common(classType, where, joinWrappers);
        return this;
    }

    /**
     * 生成计数语句
     *
     * @param classType
     * @param where
     * @param joinWrappers
     * @param groupList
     * @return
     */
    public HqlBuilder buildCountHql(Class classType, List<JpaCondition> where, List<JoinWrapper> joinWrappers, List<AliasGroupList> groupList) {
        if (groupList != null && groupList.size() > 0) {
            this.append(GROUP_COUNT_HQL[0]);
            int index = 0;
            for (AliasGroupList group : groupList) {
                if (index++ > 0) {
                    this.append(COMMA);
                }
                this.aliasSpotName(group);
            }
            this.append(GROUP_COUNT_HQL[1]);
        } else {
            this.append(COUNT_HQL);
        }
        this.common(classType, where, joinWrappers);
        return this;
    }

    /**
     * 生成删除语句
     *
     * @param classType
     * @param where
     * @return
     */
    public HqlBuilder buildDeleteHql(Class classType, List<JpaCondition> where) {
        this.append(DELETE_HQL);
        this.common(classType, where, null);
        return this;
    }

    /**
     * 生成更新语句
     *
     * @param classType
     * @param where
     * @param updateValues
     * @return
     */
    public HqlBuilder buildUpdateHql(Class classType, List<JpaCondition> where, List<JpaCondition> updateValues) {
        this.space(SqlKeyword.UPDATE.name());
        this.append(classType.getName());
        this.space(ALIAS_FLAG);
        this.space(SqlKeyword.SET.name());
        this.updateValue(updateValues);
        this.where();
        this.condition(where);
        return this;
    }

    /**
     * 生成连接语句
     * LEFT JOIN ENTITY T2 ON T1.AAA=T2.BBB AND T2.BBB = :BBB AND T2.BBB = :BBB
     * @param joinType
     * @param classType
     * @param aliasIndex
     * @param myselfProperty
     * @param joinProperty
     * @param whereValues
     * @return
     */
    public HqlBuilder buildJoinHql(SqlJoinType joinType, Class classType,
                                   int aliasIndex, String myselfProperty,
                                   String joinProperty, List<JpaCondition> whereValues) {
        this.space(joinType.getValue());
        this.append(classType.getName());
        this.append(SPACE);
        this.append(ALIAS_FLAG);
        this.append(aliasIndex);
        this.append(SPACE);
        this.space(SqlKeyword.ON.name());
        this.space(joinProperty);
        this.space(SqlExpression.EQUALS.getValue());
        this.space(myselfProperty);
        if (whereValues == null || whereValues.size() == 0) {
            return this;
        }
        for (JpaCondition cond : whereValues) {
            this.space(SqlKeyword.AND.name());
            this.aliasSpotName(cond);
            this.expression(cond.getExpression(), cond.getPropertyParam());
        }
        return this;
    }

    /**
     * 查询视图处理 以hql new model 形式创建语句
     * SELECT NEW ClassName(XXX,XXX) FROM
     *
     * @param queryView
     * @param aliasMaps
     */
    private void queryViewHandle(QueryView<?> queryView, Map<Class<? extends IEntity>, Integer> aliasMaps) {
        this.append(SqlKeyword.SELECT.name());
        this.append(NEW_VIEW_HQL[0]);
        this.append(queryView.getViewClassType().getName());
        this.append(NEW_VIEW_HQL[1]);
        int index = 0;

        // 如果使用了聚合函数，则构造函数由聚合函数的字段组成
        // 否则使用查询视图指定的构造函数来组成
        if (queryView.isAggregatesView()) {
            //聚合函数视图
            for (AliasGroupList groupList : queryView.getGroups()) {
                if (index++ > 0) {
                    this.append(COMMA);
                }
                this.aliasSpotName(groupList);
            }
            for (AliasAggregateList aggregateList : queryView.getAggregates()) {
                if (index++ > 0) {
                    this.append(COMMA);
                }
                this.append(SPACE);
                this.append(aggregateList.getAggregateType().name());
                this.append(BRACKETS[0]);
                this.aggregateSpotName(aggregateList);
                this.append(BRACKETS[1]);
            }
        } else {
            for (Class<? extends IEntity> aClass : queryView.getUseConstructor().getParameterTypes()) {
                if (index++ > 0) {
                    this.append(COMMA);
                }
                this.append(ALIAS_FLAG);
                Integer aliasIndex = aliasMaps.get(aClass);
                if (aliasIndex == null) {
                    throw new JpaSQLException("查询语句中未关联" + aClass.getSimpleName() + "类");
                }
                if (aliasIndex != 0) {
                    this.append(aliasIndex);
                }
            }
        }
        this.append(NEW_VIEW_HQL[2]);
    }

    /**
     * 连接处理
     *
     * @param joinWrappers
     */
    private void join(List<JoinWrapper> joinWrappers) {
        if (joinWrappers == null || joinWrappers.size() == 0) {
            return;
        }
        for (JoinWrapper joinWrapper : joinWrappers) {
            String joinSql = joinWrapper.getJoinSql();
            this.append(joinSql);
        }
    }

    /**
     * 公共处理
     *
     * @param classType
     * @param where
     * @param joinWrappers
     */
    private void common(Class classType, List<JpaCondition> where, List<JoinWrapper> joinWrappers) {
        this.append(classType.getName());
        this.space(ALIAS_FLAG);
        this.join(joinWrappers);
        if (where.size() > 0) {
            this.where();
            this.condition(where);
        }
    }

    /**
     * 条件处理
     *
     * @param list
     */
    private void condition(List<JpaCondition> list) {
        int index = 0;
        for (JpaCondition cond : list) {
            if (index++ > 0) {
                this.space(SqlKeyword.AND.name());
            }
            this.aliasSpotName(cond);
            this.expression(cond.getExpression(), cond.getPropertyParam());
        }
    }

    /**
     * 更新值处理
     *
     * @param list
     */
    private void updateValue(List<JpaCondition> list) {
        int index = 0;
        for (JpaCondition cond : list) {
            if (index++ > 0) {
                this.append(COMMA);
            }
            this.aliasSpotName(cond);
            this.expression(cond.getExpression(), cond.getUpdateValueName());
        }
    }

    /**
     * 空格处理
     *
     * @param value
     */
    private void space(String value) {
        this.append(SPACE);
        this.append(value);
        this.append(SPACE);
    }

    /**
     * 聚合字段名称处理
     *
     * @param aggregateList
     */
    private void aggregateSpotName(AliasAggregateList aggregateList) {
        this.append(ALIAS_FLAG);
        if (aggregateList.getAliasIndex() != 0) {
            this.append(aggregateList.getAliasIndex());
        }
        this.append(SPOT);
        this.append(aggregateList.getPropertyName());
    }

    /**
     * 别名.属性字段名称 T1.test
     *
     * @param iAlias
     */
    private void aliasSpotName(IAlias iAlias) {
        this.append(ALIAS_FLAG);
        if (iAlias.getAliasIndex() != 0) {
            this.append(iAlias.getAliasIndex());
        }
        this.append(SPOT);
        this.append(iAlias.getPropertyName());
    }

    private void where() {
        this.space(SqlKeyword.WHERE.name());
    }

    private void append(String value) {
        this.hql.append(value);
    }

    private void append(int index) {
        this.hql.append(index);
    }

    /**
     * 表达式处理
     *
     * @param expression
     * @param paramName
     */
    private void expression(SqlExpression expression, String paramName) {
        this.append(expression.getValue());
        if (expression.isNeedParamValue()){
            this.append(COLON);
            this.append(paramName);
            this.append(expression.getEndValue());
        }
    }

    @Override
    public String toString() {
        String sqlString = this.hql.toString();
        this.hql = new StringBuilder();
        return sqlString;
    }
}
