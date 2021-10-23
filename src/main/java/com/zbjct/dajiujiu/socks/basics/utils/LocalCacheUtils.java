package com.zbjct.dajiujiu.socks.basics.utils;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 本地缓存
 */
public abstract class LocalCacheUtils {

    private static final ConcurrentHashMap<String, Object> CACHE = new ConcurrentHashMap<>(50);


    /**
     * 缓存使用
     *
     * @param prefix   缓存KEY的前缀
     * @param key      缓存的KEY
     * @param callback 回调函数，返回值会存放在缓存中
     * @return
     */
    public static <T> T getCache(String prefix, String key, Supplier<T> callback) {
        key = String.format("%s_%s", prefix, key);
        T result = (T) CACHE.get(key);
        if (Objects.isNull(result)) {
            result = callback.get();
            if (Objects.nonNull(result)) {
                CACHE.put(key, result);
            }
        }
        return result;
    }
}
