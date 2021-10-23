package com.zbjct.dajiujiu.socks.basics.database.impl;

import com.zbjct.dajiujiu.socks.basics.constant.Constant;
import com.zbjct.dajiujiu.socks.basics.database.JpaService;
import com.zbjct.dajiujiu.socks.basics.database.define.HqlBuilder;
import com.zbjct.dajiujiu.socks.basics.database.define.IEntity;
import com.zbjct.dajiujiu.socks.basics.database.define.ILogicDelete;
import com.zbjct.dajiujiu.socks.basics.database.define.PropertyFunc;
import com.zbjct.dajiujiu.socks.basics.database.define.bean.*;
import com.zbjct.dajiujiu.socks.basics.database.define.constant.SqlAggregateType;
import com.zbjct.dajiujiu.socks.basics.database.define.constant.SqlExpression;
import com.zbjct.dajiujiu.socks.basics.database.define.constant.SqlJoinType;
import com.zbjct.dajiujiu.socks.basics.define.entity.DateEntity;
import com.zbjct.dajiujiu.socks.basics.define.entity.UserEntity;
import com.zbjct.dajiujiu.socks.basics.define.param.OrderParam;
import com.zbjct.dajiujiu.socks.basics.define.vo.PageVo;
import com.zbjct.dajiujiu.socks.basics.exception.BusinessException;
import com.zbjct.dajiujiu.socks.basics.exception.JpaSQLException;
import com.zbjct.dajiujiu.socks.basics.exception.PlatformException;
import com.zbjct.dajiujiu.socks.basics.helper.Check;
import com.zbjct.dajiujiu.socks.basics.helper.SessionHelper;
import com.zbjct.dajiujiu.socks.basics.utils.ClassUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.util.*;

/**
 * JPA封装
 *
 * @param <T>
 */
@Component
public class JpaWrapper<T extends IEntity> {

    private static JpaService SERVICE;
    @Autowired
    private JpaService jpaService;//JPA服务

    @PostConstruct
    public void init() {
        JpaWrapper.SERVICE = jpaService;
    }

    private HqlBuilder hql;//hql语句
    private List<JpaCondition> whereValues; //条件
    private List<JpaCondition> updateValues;//单字段更新值
    private List<AliasOrderList> orders;//排序
    private List<AliasGroupList> groups;//分组
    private List<AliasAggregateList> aggregates;//聚合函数
    private Integer limitNumber;//页数
    private Integer limitSize;//每页条数

    private List<JoinWrapper> joinWrappers;//连接封装
    private Map<Class<? extends IEntity>, Integer> aliasMaps;//别名组 用于存放不同数据表的别名序号

    private Class<T> entityClassType;//实体类型
    private T entityObject;//当前实体实例
    private QueryView queryView;//查询视图

    //默认为关闭状态 打开时，进行的删除为逻辑删除，查询条件仅查询未被逻辑删除的记录
    private boolean logicSwitch;//逻辑开关

    private JpaWrapper() {
    }

    /**
     * 封装必须指定实体即关联的数据表
     *
     * @param entityClassType
     */
    public JpaWrapper(Class<T> entityClassType) {
        this.hql = new HqlBuilder();
        this.whereValues = new ArrayList<>(10);
        this.updateValues = new ArrayList<>(10);
        this.orders = new ArrayList<>(3);
        this.groups = new ArrayList<>(3);
        this.aggregates = new ArrayList<>(3);
        this.entityClassType = entityClassType;
        this.aliasMaps = new HashMap<>(5);
        this.aliasMaps.put(entityClassType, 0);

        //如果实现了逻辑删除接口，则打开逻辑开关
        this.logicSwitch = ILogicDelete.class.isAssignableFrom(entityClassType);
    }

    public static <NT extends IEntity<NT>> JpaWrapper<NT> create(Class<NT> entityClassType) {
        return new JpaWrapper<>(entityClassType);
    }

    /**
     * 获取JPA服务
     *
     * @return
     */
    public static JpaService getService() {
        return SERVICE;
    }

    /**
     * 设置实例
     * 当JpaWrapper通过静态方法create创建时，可以通过该方法手动设置指定实体的实例
     *
     * @param entityObject
     */
    public void setEntityObject(T entityObject) {
        this.entityObject = entityObject;
    }

    /**
     * 获取实体类型
     *
     * @return
     */
    public Class<T> getEntityClassType() {
        return entityClassType;
    }

    /**
     * 关闭逻辑开关
     *
     * @return
     */
    public JpaWrapper<T> closeLogicSwitch() {
        this.logicSwitch = false;
        return this;
    }

    /**
     * 直连接
     *
     * @param entityClassType 需要连接的实体类
     * @param <NT>
     * @return
     */
    public <NT extends IEntity<NT>> JoinWrapper<NT, T, T> join(Class<NT> entityClassType) {
        return this.join(entityClassType, SqlJoinType.INNER);
    }

    /**
     * 左连接
     *
     * @param entityClassType 需要连接的实体类
     * @param <NT>
     * @return
     */
    public <NT extends IEntity<NT>> JoinWrapper<NT, T, T> leftJoin(Class<NT> entityClassType) {
        return this.join(entityClassType, SqlJoinType.LEFT);
    }

    /**
     * 连接实体 即关联查询指定表
     *
     * @param entityClassType 需要连接的实体类
     * @param joinType        关联类型
     * @param <NT>
     * @return
     */
    public <NT extends IEntity<NT>> JoinWrapper<NT, T, T> join(Class<NT> entityClassType, SqlJoinType joinType) {
        int nextAliasIndex = this.getNextAliasIndex();
        JoinWrapper<NT, T, T> wrapper = new JoinWrapper(
                entityClassType, this,
                this.entityClassType, joinType, nextAliasIndex);
        this.addJoin(nextAliasIndex, wrapper);
        return wrapper;
    }

    /**
     * 指定字段按正向排序
     *
     * @param properties
     * @return
     */
    @SafeVarargs
    public final JpaWrapper<T> orderByAsc(PropertyFunc<T, ?>... properties) {
        return this.orderHandle(0, true, properties);
    }

    /**
     * 指定的连接表中的字段按正向排序
     *
     * @param joinClassType 本次关联查询中的实体类
     * @param properties
     * @param <NT>
     * @return
     */
    @SafeVarargs
    public final <NT extends IEntity<NT>> JpaWrapper<T> orderByAsc(Class<NT> joinClassType, PropertyFunc<NT, ?>... properties) {
        int aliasIndex = this.getAliasIndex(joinClassType);
        return this.orderHandle(aliasIndex, true, properties);
    }

    /**
     * 指定字段按逆向排序
     *
     * @param properties
     * @return
     */
    @SafeVarargs
    public final JpaWrapper<T> orderByDesc(PropertyFunc<T, ?>... properties) {
        return this.orderHandle(0, false, properties);
    }

    /**
     * 指定的连接表中的字段按逆向排序
     *
     * @param joinClassType 本次关联查询中的实体类
     * @param properties
     * @param <NT>
     * @return
     */
    @SafeVarargs
    public final <NT extends IEntity<NT>> JpaWrapper<T> orderByDesc(Class<NT> joinClassType, PropertyFunc<NT, ?>... properties) {
        int aliasIndex = this.getAliasIndex(joinClassType);
        return this.orderHandle(aliasIndex, false, properties);
    }

    /**
     * 排序处理
     *
     * @param aliasIndex
     * @param asc
     * @param properties
     * @return
     */
    private JpaWrapper<T> orderHandle(int aliasIndex, boolean asc, PropertyFunc<?, ?>... properties) {
        for (PropertyFunc<?, ?> property : properties) {
            this.orders.add(new AliasOrderList(aliasIndex, ClassUtils.getFieldName(property), asc));
        }
        return this;
    }

    /**
     * 根据分页查询排序参数列表添加封装中的排序集合
     *
     * @param orders 分页查询排序参数列表
     * @return
     */
    public JpaWrapper<T> addOrders(List<OrderParam> orders) {
        if (orders != null && orders.size() > 0) {
            orders.forEach(i -> {
                this.orders.add(new AliasOrderList(0, i.getProperty(), i.isAsc()));
            });
        }
        return this;
    }

    /**
     * 根据指定字段分组
     *
     * @param properties
     * @return
     */
    @SafeVarargs
    public final JpaWrapper<T> groupBy(PropertyFunc<T, ?>... properties) {
        for (PropertyFunc<T, ?> property : properties) {
            this.groups.add(new AliasGroupList(0, ClassUtils.getFieldName(property)));
        }
        return this;
    }

    /**
     * 根据连接表中的指定字段分组
     *
     * @param joinClassType 本次关联查询中的实体类
     * @param properties
     * @param <NT>
     * @return
     */
    @SafeVarargs
    public final <NT extends IEntity<NT>> JpaWrapper<T> groupBy(Class<NT> joinClassType, PropertyFunc<NT, ?>... properties) {
        int aliasIndex = this.getAliasIndex(joinClassType);
        for (PropertyFunc<NT, ?> property : properties) {
            this.groups.add(new AliasGroupList(aliasIndex, ClassUtils.getFieldName(property)));
        }
        return this;
    }

    /**
     * 添加一个sum的聚合函数
     *
     * @param properties
     * @return
     */
    @SafeVarargs
    public final JpaWrapper<T> sum(PropertyFunc<T, ?>... properties) {
        return this.addAggregate(SqlAggregateType.SUM, properties);
    }

    /**
     * 为连接表中的字段添加一个sum的聚合函数
     *
     * @param joinClassType 本次关联查询中的实体类
     * @param properties
     * @param <NT>
     * @return
     */
    @SafeVarargs
    public final <NT extends IEntity<NT>> JpaWrapper<T> sum(Class<NT> joinClassType, PropertyFunc<NT, ?>... properties) {
        return this.addAggregate(SqlAggregateType.SUM, joinClassType, properties);
    }

    /**
     * 添加指定聚合类型的聚合函数
     *
     * @param type       聚合函数类型
     * @param properties
     * @return
     */
    @SafeVarargs
    public final JpaWrapper<T> addAggregate(SqlAggregateType type, PropertyFunc<T, ?>... properties) {
        for (PropertyFunc<T, ?> property : properties) {
            this.aggregates.add(new AliasAggregateList(0, ClassUtils.getFieldName(property), type));
        }
        return this;
    }

    /**
     * 为连接表中的字段添加指定聚合类型的聚合函数
     *
     * @param type          聚合函数类型
     * @param joinClassType 本次关联查询中的实体类
     * @param properties
     * @param <NT>
     * @return
     */
    @SafeVarargs
    public final <NT extends IEntity<NT>> JpaWrapper<T> addAggregate(SqlAggregateType type, Class<NT> joinClassType, PropertyFunc<NT, ?>... properties) {
        int aliasIndex = this.getAliasIndex(joinClassType);
        for (PropertyFunc<NT, ?> property : properties) {
            this.aggregates.add(new AliasAggregateList(aliasIndex, ClassUtils.getFieldName(property), type));
        }
        return this;
    }

    /**
     * 保存
     *
     * @return
     */
    public T doSave() {
        return SERVICE.save(this.getEntityObject());
    }

    /**
     * 检查是否存在
     *
     * @return
     */
    public boolean doCheckExists() {
        return SERVICE.exist(this);
    }

    /**
     * 检查是否存在 不存在时抛出错误信息异常
     *
     * @param errorMessage 错误消息
     */
    public void doCheckExists(String errorMessage) {
        if (this.doCheckExists()) {
            throw new BusinessException(errorMessage);
        }
    }

    /**
     * 检查是否不存在 存在时抛出错误信息异常
     *
     * @param errorMessage 错误消息
     */
    public void doCheckNotExists(String errorMessage) {
        if (!this.doCheckExists()) {
            throw new BusinessException(errorMessage);
        }
    }

    /**
     * 单条查询
     *
     * @return
     */
    public T doGetOne() {
        return SERVICE.get(this);
    }

    /**
     * 单条查询并检查是否为空
     *
     * @return
     */
    public T doGetOneAndCheckNull() {
        return this.doGetOneAndCheckNull(this.getClassDescribe());
    }

    /**
     * 单条查询并检查是否为空
     *
     * @param resultDescribe 返回内容描述
     * @return
     */
    public T doGetOneAndCheckNull(String resultDescribe) {
        T t = this.doGetOne();
        Check.notNull(t, "无效的" + resultDescribe);
        return t;
    }

    /**
     * 单条查询并指定返回的视图
     *
     * @param resultClassType 返回视图类型
     * @param <R>
     * @return
     */
    public <R> R doGetOne(Class<R> resultClassType) {
        if (resultClassType.equals(entityClassType)) {
            return (R) this.doGetOne();
        }
        this.queryView = new QueryView(resultClassType, aliasMaps, groups, aggregates);
        return SERVICE.get(this, resultClassType);
    }

    /**
     * 列表查询
     *
     * @return
     */
    public List<T> doList() {
        return SERVICE.list(this);
    }

    /**
     * 列表查询 并指定返回的列表
     *
     * @param resultClassType 返回视图类型
     * @param <R>
     * @return
     */
    public <R> List<R> doList(Class<R> resultClassType) {
        if (resultClassType.equals(entityClassType)) {
            return (List<R>) this.doList();
        }
        this.queryView = new QueryView(resultClassType, aliasMaps, groups, aggregates);
        return SERVICE.list(this, resultClassType);
    }

    /**
     * 分页查询
     *
     * @return
     */
    public PageVo<T> doPage() {
        return SERVICE.page(this);
    }

    /**
     * 分页查询
     *
     * @param resultClassType 返回视图类型
     * @param <R>
     * @return
     */
    public <R> PageVo<R> doPage(Class<R> resultClassType) {
        if (resultClassType.equals(entityClassType)) {
            return (PageVo<R>) this.doPage();
        }
        this.queryView = new QueryView(resultClassType, aliasMaps, groups, aggregates);
        return SERVICE.page(this, resultClassType);
    }

    /**
     * 统计条数
     *
     * @return
     */
    public long doCount() {
        return SERVICE.count(this);
    }

    /**
     * 删除
     *
     * @return
     */
    public int doDelete() {
        if (logicSwitch) {
            this.updateValues.add(new JpaCondition(ILogicDelete.LOGIC_DELETE_FIELD_NAME,
                    ILogicDelete.TRUE));
            return SERVICE.updateValue(this);
        }
        return SERVICE.delete(this);
    }

    /**
     * 更新
     * 需要通过 addUpdateValue 方法增加需要更新的值
     *
     * @return
     */
    public int doUpdate() {
        return SERVICE.updateValue(this);
    }


    /**
     * 设置条数
     *
     * @param size 条数
     * @return
     */
    public JpaWrapper<T> limit(int size) {
        this.limitSize = size;
        return this;
    }

    /**
     * 设置页数与条数
     *
     * @param number 页数
     * @param size   条数
     * @return
     */
    public JpaWrapper<T> limit(int number, int size) {
        this.limitNumber = number <= 0 ? 1 : number;
        this.limitSize = size;
        return this;
    }

    /**
     * 添加 更新值
     * 该方法仅用于调用 doUpdate 更新方法时有用
     * 可以重复调用该方法以添加多个需要更新的字段
     *
     * @param property 指定更新的字段
     * @param value    指定更新的值
     * @return
     */
    public JpaWrapper<T> addUpdateValue(PropertyFunc<T, ?> property, Object value) {
        this.updateValues.add(new JpaCondition(property, value));
        return this;
    }

    /**
     * 添加 更新值
     * （该方法不需要指定更新值，会从Wrapper的实例中根据属性字段函数指定的字段获取）
     * 该方法仅用于调用 doUpdate 更新方法时有用
     * 可以重复调用该方法以添加多个需要更新的字段
     *
     * @param property
     * @return
     */
    public JpaWrapper<T> addUpdateValue(PropertyFunc<T, ?> property) {
        Object apply = property.apply(this.getEntityObject());
        return this.addUpdateValue(property, apply);
    }

    /**
     * 添加一个表达式为 EQUALS 的条件
     *
     * @param property 字段函数
     * @param value    条件值
     * @return
     */
    public JpaWrapper<T> where(PropertyFunc<T, ?> property, Object value) {
        return this.where(property, value, SqlExpression.EQUALS);
    }

    /**
     * 添加一个表达式为 EQUALS 的条件
     *
     * @param property  字段函数
     * @param value     条件值
     * @param condition 判断条件，condition为true 时才增加条件
     * @return
     */
    public JpaWrapper<T> where(boolean condition, PropertyFunc<T, ?> property, Object value) {
        if (!condition) {
            return this;
        }
        return this.where(property, value, SqlExpression.EQUALS);
    }

    /**
     * 添加一个表达式为 指定表达式 的条件
     *
     * @param property   字段函数
     * @param value      条件值
     * @param expression 指定表达式
     * @return
     */
    public JpaWrapper<T> where(PropertyFunc<T, ?> property, Object value, SqlExpression expression) {
        return this.addCondition(ClassUtils.getFieldName(property), value, expression);
    }

    /**
     * 添加一个表达式为 指定表达式 的条件
     *
     * @param property   字段函数
     * @param value      条件值
     * @param expression 指定表达式
     * @param condition  判断条件，condition为true 时才增加条件
     * @return
     */
    public JpaWrapper<T> where(boolean condition, PropertyFunc<T, ?> property, Object value, SqlExpression expression) {
        if (!condition) {
            return this;
        }
        return this.addCondition(ClassUtils.getFieldName(property), value, expression);
    }

    /**
     * 添加一个表达式为 指定表达式 的条件
     *
     * @param field      指定字段 该字段必须为当前Wrapper指向的实体类中的字段
     * @param value      条件值
     * @param expression 指定表达式
     * @return
     */
    public JpaWrapper<T> where(Field field, Object value, SqlExpression expression) {
        return this.addCondition(field.getName(), value, expression);
    }

    /**
     * 添加一个表达式为 EQUALS 的条件
     * （该方法不需要指定条件值，条件值会从Wrapper的实例中根据属性字段函数指定的字段获取）
     *
     * @param property 字段函数
     * @return
     */
    public JpaWrapper<T> where(PropertyFunc<T, ?> property) {
        return this.where(property, SqlExpression.EQUALS);
    }

    /**
     * 添加一个表达式为 指定表达式 的条件
     * （该方法不需要指定条件值，条件值会从Wrapper的实例中根据属性字段函数指定的字段获取）
     *
     * @param property   字段函数
     * @param expression 指定表达式
     * @return
     */
    public JpaWrapper<T> where(PropertyFunc<T, ?> property, SqlExpression expression) {
        Object apply = property.apply(this.getEntityObject());
        return this.where(property, apply, expression);
    }

    /**
     * 添加一个条件为 companyId = 当前公司id 的条件
     *
     * @return
     */
    public JpaWrapper<T> presentCompany() {
        Field field = ClassUtils.getField(this.entityClassType, Constant.COMPANY_ID_FIELD_NAME);
        if (field == null) {
            throw new PlatformException("[%s]不存在公司Id字段");
        }
        if (this.entityObject != null) {
            ClassUtils.setFieldValue(this.entityObject,
                    field, SessionHelper.getCompanyId());
        }
        this.where(field, SessionHelper.getCompanyId(), SqlExpression.EQUALS);
        return this;
    }

    /**
     * 为连接的实体类添加一个表达式为 EQUALS 的条件
     *
     * @param joinClassType 连接的实体类
     * @param property      连接实体类的字段函数
     * @param value
     * @param <NT>
     * @return
     */
    public <NT extends IEntity<NT>> JpaWrapper<T> where(Class<NT> joinClassType, PropertyFunc<NT, ?> property, Object value) {
        return this.where(joinClassType, property, value, SqlExpression.EQUALS);
    }

    /**
     * 为连接的实体类添加一个表达式为 指定表达式 的条件
     *
     * @param joinClassType 连接的实体类
     * @param property      连接实体类的字段函数
     * @param value
     * @param <NT>
     * @return
     */
    public <NT extends IEntity<NT>> JpaWrapper<T> where(Class<NT> joinClassType, PropertyFunc<NT, ?> property, Object value, SqlExpression expression) {
        int aliasIndex = this.getAliasIndex(joinClassType);
        this.addCondition(ClassUtils.getFieldName(property), value, expression, aliasIndex);
        return this;
    }

    /**
     * 获取实例
     *
     * @return
     */
    protected T getEntityObject() {
        if (this.entityObject == null) {
            throw new JpaSQLException("ENTITY OBJECT IS NULL");
        }
        return this.entityObject;
    }

    /**
     * 获取分页页数
     *
     * @return
     */
    protected Integer getLimitNumber() {
        return limitNumber;
    }

    /**
     * 获取分页条数
     *
     * @return
     */
    protected Integer getLimitSize() {
        return limitSize;
    }

    /**
     * 获取下一个别名
     *
     * @return
     */
    protected int getNextAliasIndex() {
        return this.aliasMaps.size() + 1;
    }

    /**
     * 根据实体类型，获取别名序号
     *
     * @param joinClassType
     * @param <NT>
     * @return
     */
    protected <NT extends IEntity<NT>> int getAliasIndex(Class<NT> joinClassType) {
        Integer index = this.aliasMaps.get(joinClassType);
        if (index == null) {
            throw new JpaSQLException("not join entity class " + joinClassType.getSimpleName());
        }
        return index;
    }

    /**
     * 添加一个连接
     *
     * @param nextAliasIndex 下一个别名序号
     * @param joinWrapper    新的连接封装实例
     */
    protected void addJoin(int nextAliasIndex, JoinWrapper joinWrapper) {
        if (this.joinWrappers == null) {
            this.joinWrappers = new ArrayList<>(3);
        }
        this.joinWrappers.add(joinWrapper);
        this.aliasMaps.put(joinWrapper.entityClassType, nextAliasIndex);
    }

    /**
     * 获取实体类描述
     *
     * @return
     */
    private String getClassDescribe() {
        return ClassUtils.getClassDescribe(entityClassType);
    }

    /**
     * 逻辑删除查询处理
     */
    private void logicDeleteQueryHandle() {
        if (!this.logicSwitch) {
            return;
        }
        //逻辑开关打开时 增加条件 是否删除 = 否
        // where deleteFlag = 0
        this.addCondition(ILogicDelete.LOGIC_DELETE_FIELD_NAME,
                ILogicDelete.FALSE, SqlExpression.EQUALS);
    }

    /**
     * 获取查询HQL
     *
     * @return
     */
    protected String getSelectHql() {
        this.logicDeleteQueryHandle();
        return this.hql
                .buildSelectHql(this.entityClassType, queryView, aliasMaps
                        , whereValues, joinWrappers)
                .buildGroup(groups)
                .buildOrder(orders)
                .toString();
    }

    /**
     * 获取统计HQL
     *
     * @return
     */
    protected String getCountHql() {
        this.logicDeleteQueryHandle();
        return this.hql
                .buildCountHql(this.entityClassType, whereValues, joinWrappers, groups)
                .toString();
    }

    /**
     * 获取删除HQL
     *
     * @return
     */
    protected String getDeleteHql() {
        return this.hql.buildDeleteHql(this.entityClassType, whereValues).toString();
    }

    /**
     * 获取更新HQL
     *
     * @return
     */
    protected String getUpdateHql() {
        if (whereValues.size() == 0) {
            throw new JpaSQLException("AT LEAST ONE UPDATE CONDITIONS");
        }
        if (updateValues.size() == 0) {
            throw new JpaSQLException("AT LEAST ONE UPDATE VALUES");
        }
        if (UserEntity.class.isAssignableFrom(this.entityClassType)) {
            this.updateValues.add(new JpaCondition("updateUser", SessionHelper.getUserIdNotCheck()));
            this.updateValues.add(new JpaCondition("updateTime", new Date()));
        } else if (DateEntity.class.isAssignableFrom(this.entityClassType)) {
            this.updateValues.add(new JpaCondition("updateTime", new Date()));
        }
        return hql.buildUpdateHql(this.entityClassType, whereValues, updateValues).toString();
    }

    /**
     * 处理条件
     *
     * @param query
     */
    protected void handleCondition(Query query) {
        this.handleCondition(query, true);
    }

    /**
     * 处理条件
     *
     * @param query
     * @param limit
     */
    protected void handleCondition(Query query, boolean limit) {
        this.whereValues.forEach(w -> {
            if (w.getExpression().isNeedParamValue()) {
                query.setParameter(w.getPropertyParam(), w.getValue());
            }
        });
        if (joinWrappers != null && joinWrappers.size() > 0) {
            joinWrappers.forEach(j -> j.handleCondition(query));
        }
        if (limit) {
            this.limitHandle(query);
        }
    }

    /**
     * 处理更新值
     *
     * @param query
     */
    protected void handleUpdateValue(Query query) {
        this.updateValues.forEach(w -> {
            query.setParameter(w.getUpdateValueName(), w.getValue());
        });
    }

    /**
     * 处理条数
     *
     * @param query
     */
    private void limitHandle(Query query) {
        if (limitSize == null) {
            if (whereValues.size() == 0 && (joinWrappers == null || joinWrappers.size() == 0) && groups.size() == 0) {
                throw new JpaSQLException("NOT ALLOW FULL TABLE SCANNING");
            }
            return;
        }
        if (limitNumber == null) {
            query.setMaxResults(limitSize);
            return;
        }
        int beginIndex = (limitNumber - 1) * limitSize;
        query.setFirstResult(beginIndex);
        query.setMaxResults(limitSize);
    }

    /**
     * 增加条件
     *
     * @param propertyName
     * @param value
     * @param expression
     * @return
     */
    private JpaWrapper addCondition(String propertyName, Object value, SqlExpression expression) {
        this.addCondition(propertyName, value, expression, 0);
        return this;
    }

    /**
     * 增加条件
     *
     * @param propertyName
     * @param value
     * @param expression
     * @param aliasIndex
     */
    protected void addCondition(String propertyName, Object value, SqlExpression expression, int aliasIndex) {
        this.whereValues.add(new JpaCondition(this.whereValues.size(), propertyName, value, expression, aliasIndex));
    }
}
