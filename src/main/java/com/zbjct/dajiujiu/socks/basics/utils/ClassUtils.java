package com.zbjct.dajiujiu.socks.basics.utils;

import com.zbjct.dajiujiu.socks.basics.constant.Constant;
import com.zbjct.dajiujiu.socks.basics.constant.ResultCode;
import com.zbjct.dajiujiu.socks.basics.database.define.PropertyFunc;
import com.zbjct.dajiujiu.socks.basics.exception.PlatformException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 类辅助工具
 *
 * @author
 */
@Slf4j
public abstract class ClassUtils {

    /**
     * 获取类描述
     * 如果是一个swagger ApiModel 标注的类，则获取描述
     *
     * @param classType
     * @return
     */
    public static String getClassDescribe(Class<?> classType) {
        ApiModel ann = classType.getAnnotation(ApiModel.class);
        return ann == null ? classType.getSimpleName() : ann.description();
    }

    /**
     * 获取字段描述
     * 如果是一个swagger ApiModelProperty 标注的字段，则获取描述值
     *
     * @param classType
     * @param fieldName
     * @return
     */
    public static String getFieldDescribe(Class<?> classType, String fieldName) {
        Field field = getField(classType, fieldName);
        if (field == null) {
            return fieldName;
        }
        return getFieldDescribe(field);
    }
    /**
     * 获取字段描述
     * 如果是一个swagger ApiModelProperty 标注的字段，则获取描述值
     *
     * @return
     */
    public static String getFieldDescribe(Field field) {
        ApiModelProperty ann = field.getAnnotation(ApiModelProperty.class);
        if (ann == null) {
            return field.getName();
        }
        String value = ann.value();
        return StringUtils.isBlank(value) ? field.getName() : value;
    }

    /**
     * 获取字段名称
     * 根据字段属性函数转换为字段名称
     *
     * @param field 字段属性函数
     * @return
     */
    public static String getFieldName(PropertyFunc<?, ?> field) {
        return getFieldName(field.getClass(), field);
    }

    //字段属性函数转换为字段名称
    private static String getFieldName(Class<?> classType, PropertyFunc<?, ?> field) {
        try {
            Method method = classType.getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            SerializedLambda lambda = (SerializedLambda) method.invoke(field);
            return getFieldName(lambda.getImplMethodName());
        } catch (Exception e) {
            if (!Constant.OBJECT.equals(classType.getSuperclass().getSimpleName())) {
                return getFieldName(classType.getSuperclass(), field);
            }
            log.error("字段属性函数转换为字段名称异常:", e);
            throw new PlatformException(ResultCode.R00000);
        }
    }

    /**
     * 获取类名称
     * 根据字段属性函数转换为字段所在类类名称
     *
     * @param field 字段
     * @return
     */
    public static String getClassName(PropertyFunc<?, ?> field) {
        try {
            Method method = field.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            SerializedLambda lambda = (SerializedLambda) method.invoke(field);
            String[] packages = lambda.getInstantiatedMethodType().split("\\)")[0].replaceAll(";", "").split("/");
            return packages[packages.length - 1];
        } catch (ReflectiveOperationException e) {
            log.error("字段属性函数转换为所在类类名称异常:", e);
            throw new PlatformException(ResultCode.R00000);
        }
    }

    /**
     * 根据get方法名称获取字段名称
     *
     * @param methodName
     * @return
     */
    public static String getFieldName(String methodName) {
        return StringUtils.uncapitalize(methodName.startsWith("get")
                ? methodName.substring(3)
                : (methodName.startsWith("is") ? methodName.substring(2) : methodName));
    }

    /**
     * 获取指定对象中指定对象值
     *
     * @param target 对象
     * @param field  字段
     * @return
     */
    public static Object getFieldValue(Object target, Field field) {
        if (target == null || field == null) {
            return null;
        }
        try {
            field.setAccessible(true);
            return field.get(target);
        } catch (Exception e) {
            log.error(String.format("无法获取字段值:%s.%s",
                    target.getClass().getSimpleName(), field.getName()), e);
            throw new PlatformException(ResultCode.R00000);
        }
    }

    /**
     * 设置指定对象中指定值
     *
     * @param target
     * @param field
     * @param value
     */
    public static void setFieldValue(Object target, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            log.error(String.format("无法设置字段值:%s.%s",
                    target.getClass().getSimpleName(), field.getName()), e);
            throw new PlatformException(ResultCode.R00000);
        }
    }

    /**
     * 获取目标类的字段
     *
     * @param classType  目标类
     * @param annClass   注解
     * @param LoadSupper 是否加载子类
     * @return
     */
    public static List<Field> getAllFields(Class<?> classType, Class<? extends Annotation> annClass, boolean LoadSupper) {
        //是否含注解
        String pre = Objects.isNull(annClass) ?
                "all_field_" : ("ann_field_" + annClass.getName());
        //是否加载子集
        pre = pre + (LoadSupper ? "1_" : "0_");
        return LocalCacheUtils.getCache(pre, classType.getName(), () -> {
            List<Field> fields = new ArrayList<>(Arrays.asList(classType.getDeclaredFields()));
            Class<?> upCls = classType;
            if (LoadSupper) {
                while (!Constant.OBJECT.equals(upCls.getSimpleName())) {
                    upCls = upCls.getSuperclass();
                    if (upCls.getDeclaredFields().length <= 0) {
                        continue;
                    }
                    fields.addAll(Arrays.asList(upCls.getDeclaredFields()));
                }
            }
            if (annClass != null) {
                fields = fields.stream().filter(x -> x.isAnnotationPresent(annClass))
                        .collect(Collectors.toList());
            }
            return fields;
        });
    }

    /**
     * 根据字段名称获取指定类中的字段
     *
     * @param classType
     * @param fieldName
     * @return
     */
    public static Field getField(Class<?> classType, String fieldName) {
        Map<String, Field> mapField = LocalCacheUtils.getCache("bean_all_field", classType.getName(), () -> {
            List<Field> fields = ClassUtils.getAllFields(classType, null, true);
            if (CollectionUtils.isEmpty(fields)) {
                return Collections.emptyMap();
            }
            Map<String, Field> mapFields = new HashMap<>(fields.size());
            fields.forEach(field -> mapFields.put(field.getName(), field));
            return mapFields;
        });
        return CollectionUtils.isEmpty(mapField) ? null : mapField.get(fieldName);
    }

    /**
     * 加载包prefix路径下的继承了cls类的所有子类
     *
     * @param prefix 包路径
     * @param cls    父类类型
     * @param <T>
     * @return
     */
    public static <T> Set<Class<? extends T>> load(String prefix, Class<T> cls) {
        Reflections reflections = new Reflections(prefix);
        return reflections.getSubTypesOf(cls);
    }
}
