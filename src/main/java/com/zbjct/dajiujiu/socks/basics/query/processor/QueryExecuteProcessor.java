package com.zbjct.dajiujiu.socks.basics.query.processor;

import com.zbjct.dajiujiu.socks.basics.constant.ResultCode;
import com.zbjct.dajiujiu.socks.basics.database.define.IEntity;
import com.zbjct.dajiujiu.socks.basics.database.define.IVo;
import com.zbjct.dajiujiu.socks.basics.database.define.constant.SqlExpression;
import com.zbjct.dajiujiu.socks.basics.database.impl.JpaWrapper;
import com.zbjct.dajiujiu.socks.basics.define.param.OrderParam;
import com.zbjct.dajiujiu.socks.basics.define.param.PageParam;
import com.zbjct.dajiujiu.socks.basics.define.vo.PageVo;
import com.zbjct.dajiujiu.socks.basics.dict.base.IDict;
import com.zbjct.dajiujiu.socks.basics.exception.PlatformException;
import com.zbjct.dajiujiu.socks.basics.helper.SessionHelper;
import com.zbjct.dajiujiu.socks.basics.query.IQueryOtherLoad;
import com.zbjct.dajiujiu.socks.basics.query.IQueryProvide;
import com.zbjct.dajiujiu.socks.basics.query.QueryField;
import com.zbjct.dajiujiu.socks.basics.query.QueryType;
import com.zbjct.dajiujiu.socks.basics.utils.ClassUtils;
import com.zbjct.dajiujiu.socks.basics.utils.CommonUtils;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 查询执行处理器
 * 客户端根据路径规则，方法Query控制器中的 page、list、single 方法
 * 请求在拦截器中拦截，并分割出 实际的分组名称
 * 根据分组名称获取 QueryHandleCache 查询处理缓存
 * 解析QueryHandleCache并执行查询方法
 */
@Component
public class QueryExecuteProcessor {

    protected final static String COMPOUND_CONDITION_START = "start"; //复合条件时的开始条件
    protected final static String COMPOUND_CONDITION_END = "end";//复合条件时的结束条件
    /**
     * 年月日正则表达式
     */
    private final static String DATETIME_PATTERN = "(((\\d{4})-(0[13578]|1[02])-(0[1-9]|[12]\\d|3[01]))|((\\d{4})-(0[469]|11)-(0[1-9]|[12]\\d|30))|((\\d{4})-(02)-(0[1-9]|1\\d|2[0-8]))|((\\d{2}(0[48]|[2468][048]|[13579][26]))-(02)-(29))|(((0[48]|[2468][048]|[13579][26])00)-(02)-(29))) (([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d))";

    /**
     * 执行分页
     *
     * @param pageParam
     * @return
     */
    public PageVo executePage(PageParam<Map<String, Object>> pageParam) {
        Map<String, Object> queryParams = pageParam.getQueryParams();
        QueryHandleCache<? extends IEntity> queryHandle = QueryLoadProcessor.getQueryHandle();
        JpaWrapper<? extends IEntity> queryWrapper = this.conditionHandle(QueryType.PAGE, queryHandle, queryParams);
        queryWrapper.limit(pageParam.getNumber(), pageParam.getSize());
        this.orderHandle(queryWrapper, pageParam.getOrders());
        this.orderHandle(queryWrapper, queryHandle.getOrderParams());
        return queryWrapper.doPage(queryHandle.getResultType());
    }

    /**
     * 执行List
     *
     * @param param
     * @return
     */
    public List executeList(Map<String, Object> param) {
        QueryHandleCache<? extends IEntity> queryHandle = QueryLoadProcessor.getQueryHandle();
        JpaWrapper<? extends IEntity> queryWrapper = this.conditionHandle(QueryType.LIST, queryHandle, param);
        this.orderHandle(queryWrapper, queryHandle.getOrderParams());
        return queryWrapper.doList(queryHandle.getResultType());
    }

    /**
     * 执行单条查询
     *
     * @param param
     * @return
     */
    public Object executeSingle(Map<String, Object> param) {
        QueryHandleCache<? extends IEntity> queryHandle = QueryLoadProcessor.getQueryHandle();
        JpaWrapper<? extends IEntity> queryWrapper = this.conditionHandle(QueryType.SINGLE, queryHandle, param);
        Object o = queryWrapper.doGetOne(queryHandle.getResultType());
        if (o == null) {
            return null;
        }
        if (o instanceof IQueryOtherLoad) {
            IQueryOtherLoad childLoad = (IQueryOtherLoad) o;
            childLoad.load(o);
        }
        return o;
    }

    //条件处理
    @SneakyThrows
    private JpaWrapper<? extends IEntity> conditionHandle(QueryType queryType, QueryHandleCache<? extends IEntity> queryHandle, Map<String, Object> param) {
        if (param == null) {
            param = new HashMap<>();
        }
        if (!queryHandle.getQueryType().contains(queryType)) {
            throw new PlatformException(ResultCode.C00001, "指定方法不支持[%s]查询方式", queryType.name());
        }
        JpaWrapper<? extends IEntity> queryWrapper = new JpaWrapper<>(queryHandle.getEntityType());
        List<QueryHandleFieldCache> fields = queryHandle.getFields();
        for (QueryHandleFieldCache handle : fields) {
            if (handle.isTransientField()) {
                continue;
            }
            QueryField queryField = handle.getQueryField();
            Field field = handle.getField();
            Object value = param.get(field.getName());
            try {
                this.conditionHandle(queryWrapper, queryField, field, value);
            } catch (PlatformException e) {
                throw new PlatformException(field.getName() + " " + e.getMessage());
            }
        }
        //其它条件处理
        if (IQueryProvide.class.isAssignableFrom(queryHandle.getEntityType())) {
            IEntity iEntity = CommonUtils.mapToInstance(param, queryHandle.getEntityType());
            IQueryProvide provide = (IQueryProvide) iEntity;
            provide.otherConditionHandle(queryHandle.getQueryGroupDefineValue(), queryWrapper,
                    iEntity);
        }
        if (IQueryProvide.class.isAssignableFrom(queryHandle.getVoClassType())) {
            Object o = CommonUtils.mapToInstance(param, queryHandle.getVoClassType());
            IQueryProvide provide = (IQueryProvide) o;
            provide.otherConditionHandle(queryHandle.getQueryGroupDefineValue(), queryWrapper,
                    (IVo) o);
        }
        return queryWrapper;
    }

    //排序处理
    private void orderHandle(JpaWrapper<? extends IEntity> queryWrapper, List<OrderParam> orderParams) {
        if (orderParams == null) {
            return;
        }
        queryWrapper.addOrders(orderParams);
    }

    //条件处理
    private void conditionHandle(JpaWrapper<? extends IEntity> queryWrapper, QueryField queryField, Field field, Object value) {
        //设置当前条件值 如果设置了则直接返回
        if (this.presentHandle(queryWrapper, field, queryField)) {
            return;
        }
        //设置固定条件 如果设置了则直接返回
        if (this.fixedValueHandle(queryWrapper, field, queryField)) {
            return;
        }
        SqlExpression[] condition = queryField.condition();
        //双条件
        if (condition.length == 2) {
            if (value == null) {
                if (queryField.mustInput()) {
                    throw new PlatformException("must input");
                }
                return;
            }
            if (!(value instanceof Map)) {
                throw new PlatformException("条件为复合条件，必须包含 start 或 end ");
            }
            Map map = (Map) value;
            this.singleConditionHandle(queryWrapper, queryField, field, condition[0], map.get(COMPOUND_CONDITION_START));
            this.singleConditionHandle(queryWrapper, queryField, field, condition[1], map.get(COMPOUND_CONDITION_END));
            return;
        }
        this.singleConditionHandle(queryWrapper, queryField, field, condition[0], value);
    }

    //单个条件处理
    private void singleConditionHandle(JpaWrapper<? extends IEntity> queryWrapper,
                                       QueryField queryField, Field field, SqlExpression condition, Object value) {
        if (value != null && value instanceof String) {
            if (StringUtils.isBlank(value.toString())) {
                value = null;
            }
        }
        if (value == null && condition.isNeedParamValue()) {
            if (queryField.mustInput()) {
                throw new PlatformException("must input");
            }
            if (!queryField.queryNullable()) {
                return;
            }
        } else {
            if (Date.class.isAssignableFrom(field.getType()) && value instanceof String) {
                Pattern pattern = Pattern.compile(DATETIME_PATTERN);
                Matcher matcher = pattern.matcher(value.toString());
                try {
                    if (matcher.find()) {
                        value = DateUtils.parseDate(value.toString(), "yyyy-MM-dd HH:mm:ss");
                    } else {
                        value = DateUtils.parseDate(value.toString(), "yyyy-MM-dd");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if ((long.class.isAssignableFrom(field.getType())
                    || Long.class.isAssignableFrom(field.getType())) && value instanceof Integer) {
                value = ((Integer) value).longValue();
            } else if (BigDecimal.class.isAssignableFrom(field.getType())) {
                if (value instanceof Integer) {
                    value = new BigDecimal((Integer) value);
                } else if (value instanceof String) {
                    value = new BigDecimal((String) value);
                } else {
                    value = new BigDecimal(value.toString());
                }
            }
        }
        // in 条件
        if (SqlExpression.IN.equals(condition)) {
            if (!(value instanceof Collection<?>)) {
                throw new PlatformException("条件参数值必须为数组");
            }
            if (((Collection) value).size() == 0) {
                throw new PlatformException("条件参数值为数组时，至少包含一个元素");
            }
        }
        //枚举
        if (IDict.class.isAssignableFrom(field.getType())) {
            if (value instanceof Collection<?>) {
                List list = new ArrayList<>();
                ((Collection) value).forEach(i -> {
                    list.add(handleDict(field, i));
                });
                value = list;
            } else {
                value = handleDict(field, value);
            }
        }
        Class<? extends IEntity> entityClassType = queryWrapper.getEntityClassType();
        Field classField = ClassUtils.getField(entityClassType, field.getName());
        if (classField == null) {
            //非实体类字段，跳过
            return;
        }
        queryWrapper.where(field, value, condition);
    }

    private Object handleDict(Field field, Object value) {
        Class type = field.getType();
        IDict dict = (IDict) IDict.findByName(value.toString(), type);
        return dict;
    }

    private boolean fixedValueHandle(JpaWrapper<? extends IEntity> queryWrapper, Field field, QueryField queryField) {
        if (StringUtils.isBlank(queryField.fixedValue())) {
            return false;
        }
        Object value;
        //字段类型为字典时
        if (IDict.class.isAssignableFrom(field.getType())) {
            value = this.handleDict(field, queryField.fixedValue());
        } else if (String.class.isAssignableFrom(field.getType())) {
            value = queryField.fixedValue();
        } else if (BigDecimal.class.isAssignableFrom(field.getType())) {
            value = new BigDecimal(queryField.fixedValue());
        } else if (Integer.class.isAssignableFrom(field.getType())
                || int.class.isAssignableFrom(field.getType())) {
            value = Integer.valueOf(queryField.fixedValue());
        } else {
            throw new PlatformException("暂未支持的固定值类型！");
        }
        queryWrapper.where(field, value, SqlExpression.EQUALS);
        return true;
    }

    //设置当前 公司、用户 如果执行了，则返回true
    private boolean presentHandle(JpaWrapper<? extends IEntity> queryWrapper, Field field, QueryField queryField) {
        Object value;
        switch (queryField.present()) {
            case NONE:
                return false;
            case COMPANY:
                value = SessionHelper.getCompanyId();
                break;
            case USER:
                value = SessionHelper.getUserId();
                break;
            default:
                throw new PlatformException("无效的[当前]条件");
        }
        queryWrapper.where(field, value, SqlExpression.EQUALS);
        return true;
    }

}
