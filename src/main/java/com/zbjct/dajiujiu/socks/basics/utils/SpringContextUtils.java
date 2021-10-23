package com.zbjct.dajiujiu.socks.basics.utils;

import org.springframework.context.ApplicationContext;

/**
 * spring 上下文
 */
public abstract class SpringContextUtils {

    private static ApplicationContext applicationContext;

    /**
     * 获取上下文
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 设置上下文
     *
     * @param applicationContext
     */
    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtils.applicationContext = applicationContext;
    }

    /**
     * 通过been名称获取
     *
     * @param name
     * @return
     */
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    /**
     * 通过类型获取
     *
     * @param classType
     * @return
     */
    public static <T> T getBean(Class<T> classType) {
        return applicationContext.getBean(classType);
    }

    /**
     * 根据 been名称获取指定类型的been
     *
     * @param name
     * @param classType
     * @param <T>
     * @return
     */
    public static <T> T getBean(String name, Class<T> classType) {
        return applicationContext.getBean(name, classType);
    }

}
