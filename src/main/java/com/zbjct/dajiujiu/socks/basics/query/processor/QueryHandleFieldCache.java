package com.zbjct.dajiujiu.socks.basics.query.processor;

import com.zbjct.dajiujiu.socks.basics.query.QueryField;
import lombok.Data;

import javax.persistence.Transient;
import java.lang.reflect.Field;

/**
 * 查询处理字段缓存
 */
@Data
public class QueryHandleFieldCache {

    private Field field;//字段
    private QueryField queryField;//查询字段注解
    private boolean transientField;//非映射瞬时字段

    public QueryHandleFieldCache(Field field, QueryField queryField) {
        this.field = field;
        this.queryField = queryField;
        this.transientField = field.getAnnotation(Transient.class) != null;
    }
}
