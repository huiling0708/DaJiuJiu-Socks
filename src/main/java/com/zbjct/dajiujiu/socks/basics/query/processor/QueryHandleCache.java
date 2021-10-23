package com.zbjct.dajiujiu.socks.basics.query.processor;


import com.zbjct.dajiujiu.socks.basics.constant.ResultCode;
import com.zbjct.dajiujiu.socks.basics.database.define.IEntity;
import com.zbjct.dajiujiu.socks.basics.define.param.OrderParam;
import com.zbjct.dajiujiu.socks.basics.exception.PlatformException;
import com.zbjct.dajiujiu.socks.basics.query.QueryProvide;
import com.zbjct.dajiujiu.socks.basics.query.QuerySort;
import com.zbjct.dajiujiu.socks.basics.query.QueryType;
import io.swagger.annotations.Api;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.*;


/**
 * 查询处理缓存
 *
 * @param <T>
 */
@Data
public class QueryHandleCache<T extends IEntity<T>> {

    private String describe;//描述
    private StringBuilder noteBuilder;//方法详细描述(加上特定的查询指定内容，如该方法仅查询指定公司的记录)
    private String queryGroup;//路径 即查询分组
    private Set<QueryType> queryType;//查询类型
    private List<OrderParam> orderParams;//排序
    private Class<?> voClassType;//创建查询的vo类
    private Class<T> entityType;//实体类型
    private Class<?> resultType;//返回值类型
    private List<QueryHandleFieldCache> fields;
    private List<Field> groupField; //分组查询字段
    private String queryGroupDefineValue;//查询分组定义value 即开发人员标注的queryGroup 值
    private String tags;//控制器目录
    private boolean needLogin;//需要登陆

    public QueryHandleCache(String groupName, Class<?> voClass, QueryProvide provide) throws PlatformException {
        if (provide.entityType().equals(Object.class) ?
                !IEntity.class.isAssignableFrom(voClass) :
                !IEntity.class.isAssignableFrom(provide.entityType())) {
            throw new PlatformException("指定的实体类型无效，未实现IEntity接口");
        }
        this.voClassType = voClass;
        this.needLogin = provide.needLogin();
        this.entityType = provide.entityType().equals(Object.class) ?
                (Class<T>) voClass : (Class<T>) provide.entityType();
        this.resultType = provide.resultType().equals(Object.class) ?
                voClass : provide.resultType();

        this.describe = provide.value();
        this.queryGroupDefineValue = provide.queryGroup();
        this.queryGroup = groupName;
        this.queryType = new HashSet<>(Arrays.asList(provide.queryType()));

        Class<?> controller = provide.controller();
        if (!controller.equals(Object.class)) {
            Api api = controller.getAnnotation(Api.class);
            if (api == null) {
                throw new PlatformException(ResultCode.E, "[%s]上标注的@QueryProvide注解,controller指向的类[%s]未标注@Api",
                        voClass.getSimpleName(), controller.getSimpleName());
            }
            this.tags = api.tags()[0];
        }

        //字段
        this.fields = new ArrayList<>();
        /*分组字段*/
        this.groupField = new ArrayList<>();
        //排序
        if (provide.sort().length > 0) {
            this.orderParams = new ArrayList<>();
            for (QuerySort querySort : provide.sort()) {
                switch (querySort.direction()) {
                    case ASC:
                        this.orderParams.add(OrderParam.asc(querySort.value()));
                        break;
                    case DESC:
                        this.orderParams.add(OrderParam.desc(querySort.value()));
                        break;
                }
            }
        }
    }

}
