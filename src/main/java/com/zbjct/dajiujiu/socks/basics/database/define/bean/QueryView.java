package com.zbjct.dajiujiu.socks.basics.database.define.bean;

import com.zbjct.dajiujiu.socks.basics.database.define.IEntity;
import com.zbjct.dajiujiu.socks.basics.exception.JpaSQLException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

/**
 * 查询视图帮助 在生成hql语句时使用
 *
 * @param <R>
 */
@Getter
@Slf4j
public class QueryView<R> {

    private Class<R> viewClassType; //视图类型即返回值类型
    private Constructor useConstructor;//使用的构造函数
    private List<AliasGroupList> groups;//分组列表
    private List<AliasAggregateList> aggregates;//聚合函数列表
    private boolean aggregatesView;//是否是一个使用聚合函数的视图

    public QueryView(Class<R> viewClassType, Map<Class<? extends IEntity>, Integer> aliasMaps, List<AliasGroupList> groups, List<AliasAggregateList> aggregates) {
        this.viewClassType = viewClassType;
        this.groups = groups;
        this.aggregates = aggregates;
        if (aggregates == null || aggregates.size() == 0) {
            this.useConstructorHandle(viewClassType, aliasMaps);
            aggregatesView = false;
        } else {
            int size = groups.size() + aggregates.size();
            this.useConstructorHandle(viewClassType, size);
            aggregatesView = true;
        }
    }

    /**
     * 构造函数处理
     * 在使用聚合函数时，因为接收的参数要包含 N个分组值和N个聚合值
     * 所以，返回的查询视图中，必须提供相应的构造函数
     *
     * @param viewClassType
     * @param size
     */
    private void useConstructorHandle(Class<R> viewClassType, int size) {
        Constructor<?>[] constructors = this.viewClassType.getConstructors();
        NEXT:
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterTypes().length == size) {
                this.useConstructor = constructor;
                return;
            }
        }
        throw new JpaSQLException("在" + viewClassType.getSimpleName() + "类中,没有适合接收聚合函数返回值的构造函数。(需要包含" + getGroups().size() + "个分组值和" + getAggregates().size() + "个聚合值)");
    }

    /**
     * 构造函数处理
     *  不使用聚合函数的情况
     *  如果构造函数中使用的类在别名map中都能被找到,则使用该构造函数
     *
     * @param viewClassType
     * @param aliasMaps
     */
    private void useConstructorHandle(Class<R> viewClassType, Map<Class<? extends IEntity>, Integer> aliasMaps) {
        Constructor<?>[] constructors = this.viewClassType.getConstructors();
        NEXT:
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterTypes().length == 0) {
                continue;
            }
            if (constructor.getParameterTypes().length < aliasMaps.size()) {
                continue;
            }
            for (Class<?> paramType : constructor.getParameterTypes()) {
                if (!aliasMaps.containsKey(paramType)) {
                    continue NEXT;
                }
            }
            this.useConstructor = constructor;
            return;
        }
        throw new JpaSQLException("在" + viewClassType.getSimpleName() + "类中,未找到适合接收查询结果集的构造函数");
    }
}
