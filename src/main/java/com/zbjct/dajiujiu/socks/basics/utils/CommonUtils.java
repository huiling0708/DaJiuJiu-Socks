package com.zbjct.dajiujiu.socks.basics.utils;

import com.zbjct.dajiujiu.socks.basics.constant.ResultCode;
import com.zbjct.dajiujiu.socks.basics.dict.base.IDict;
import com.zbjct.dajiujiu.socks.basics.exception.PlatformException;
import com.zbjct.dajiujiu.socks.basics.helper.Check;
import org.springframework.util.CollectionUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 公共工具
 */
public abstract class CommonUtils {

    /**
     * 获取去下划线的uuid
     *
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * map 转为 实例
     *
     * @param map
     * @param classType
     * @param <T>
     * @return
     */
    public static <T> T mapToInstance(Map<String, Object> map, Class<T> classType) {
        Check.notNull(classType, "转换类型为空");
        T t;
        try {
            t = classType.newInstance();
        } catch (InstantiationException e) {
            throw new PlatformException(ResultCode.R00000, "[%s]类中缺少无参构造函数", classType.getSimpleName());
        } catch (IllegalAccessException e) {
            throw new PlatformException(ResultCode.R00000, "[%s]类中缺少公开无参构造函数", classType.getSimpleName());
        }
        if (map == null || map.size() == 0) {
            return t;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Field field = ClassUtils.getField(classType, key);
            if (field == null) {
                continue;
            }
            field.setAccessible(true);
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }
            if (value instanceof HashMap) {
                value = mapToInstance((Map<String, Object>) value, field.getType());
            } else if (value instanceof List) {
                ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                Type genericClassType = genericType.getActualTypeArguments()[0];
                value = ((List<Map<String, Object>>) value)
                        .stream().map(x -> mapToInstance(x, (Class<?>) genericClassType))
                        .collect(Collectors.toList());
            } else if (value instanceof String) {
                String valueStr = (String) value;
                if (Date.class.isAssignableFrom(field.getType())) {
                    String dateFormat;
                    if (valueStr.matches("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
                        dateFormat = "yyyy-MM-dd HH:mm:ss";
                    } else {
                        dateFormat = "yyyy-MM-dd";
                    }
                    try {
                        value = new SimpleDateFormat(dateFormat).parse(valueStr);
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(valueStr + "无法转换为时间只支持[yyyy-MM-dd]和[yyyy-MM-dd HH:mm:ss]的格式");
                    }
                } else if (BigDecimal.class.isAssignableFrom(field.getType())) {
                    value = new BigDecimal(valueStr);
                } else if (IDict.class.isAssignableFrom(field.getType())) {
                    Class type = field.getType();
                    value = IDict.findByName(valueStr, type);
                }
            }
            ClassUtils.setFieldValue(t, field, value);
        }
        return t;
    }

    /**
     * 复制属性，只处理相同字段
     *
     * @param instances       原实例
     * @param resultClassType 新实例类型
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> List<R> copyProperty(List<T> instances, Class<R> resultClassType) {
        if (CollectionUtils.isEmpty(instances)) {
            return Collections.emptyList();
        }
        List<R> results = new ArrayList<>(instances.size());
        try {
            for (T instance : instances) {
                results.add(copyProperty(instance, resultClassType));
            }
        } catch (Exception e) {
            throw new PlatformException(ResultCode.R00000, e.getMessage());
        }
        return results;
    }

    /**
     * 复制属性，只处理相同字段
     *
     * @param instance
     * @param resultClassType
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> R copyProperty(T instance, Class<R> resultClassType) {
        if (instance == null || resultClassType == null) {
            return null;
        }
        R result;
        try {
            result = resultClassType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new PlatformException(ResultCode.R00000, e.getMessage());
        }
        return copyProperty(instance, result);
    }

    /**
     * 复制属性，只处理相同字段
     *
     * @param instance
     * @param resultInstance
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> R copyProperty(T instance, R resultInstance) {
        if (instance == null || resultInstance == null) {
            return null;
        }
        Class<R> resultClassType = (Class<R>) resultInstance.getClass();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(instance.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            Object invoke;
            Field resultField;
            for (PropertyDescriptor p : propertyDescriptors) {
                Method readMethod = p.getReadMethod();
                if (readMethod == null || null == p.getWriteMethod()
                        || (invoke = readMethod.invoke(instance)) == null) {
                    continue;
                }
                try {
                    resultField = ClassUtils.getField(resultClassType, p.getName());
                } catch (PlatformException e) {
                    continue;
                }
                if (resultField == null) {
                    continue;
                }
                ClassUtils.setFieldValue(resultInstance, resultField, invoke);
            }
        } catch (Exception e) {
            throw new PlatformException(ResultCode.R00000, e.getMessage());
        }
        return resultInstance;
    }

}
