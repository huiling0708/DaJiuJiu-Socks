package com.zbjct.dajiujiu.socks.basics.database.define.bean;

import com.zbjct.dajiujiu.socks.basics.database.define.IAlias;
import com.zbjct.dajiujiu.socks.basics.database.define.IEntity;
import com.zbjct.dajiujiu.socks.basics.database.define.PropertyFunc;
import com.zbjct.dajiujiu.socks.basics.database.define.constant.SqlExpression;
import com.zbjct.dajiujiu.socks.basics.utils.ClassUtils;
import lombok.Getter;

/**
 * JPA查询条件封装
 *
 * @param <T>
 */
@Getter
public class JpaCondition<T extends IEntity<T>> implements IAlias {

    private Object value;//参数值
    private SqlExpression expression;//表达式
    private String propertyName;//属性名称
    private String propertyParam;//属性参数
    private String updateValueName;
    private int aliasIndex;

    public JpaCondition(PropertyFunc<T, ?> property, Object value) {
        this(0, ClassUtils.getFieldName(property), value, SqlExpression.EQUALS, 0);
    }

    public JpaCondition(String propertyName, Object value) {
        this(0, propertyName, value, SqlExpression.EQUALS, 0);
    }

    public JpaCondition(int index, String propertyName, Object value, SqlExpression expression, int aliasIndex) {
        this.expression = expression;
        this.propertyName = propertyName;
        this.propertyParam = propertyName + "_" + aliasIndex + "_" + index;
        this.updateValueName = "update_" + this.propertyParam;
        this.aliasIndex = aliasIndex;
        this.value=this.expression.valueHandle(value);
    }
}
