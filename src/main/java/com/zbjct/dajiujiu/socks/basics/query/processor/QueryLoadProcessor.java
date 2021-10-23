package com.zbjct.dajiujiu.socks.basics.query.processor;

import com.zbjct.dajiujiu.socks.basics.constant.Constant;
import com.zbjct.dajiujiu.socks.basics.database.define.IEntity;
import com.zbjct.dajiujiu.socks.basics.database.define.IVo;
import com.zbjct.dajiujiu.socks.basics.exception.PlatformException;
import com.zbjct.dajiujiu.socks.basics.helper.Check;
import com.zbjct.dajiujiu.socks.basics.helper.SessionHelper;
import com.zbjct.dajiujiu.socks.basics.query.*;
import com.zbjct.dajiujiu.socks.basics.utils.ClassUtils;
import com.zbjct.dajiujiu.socks.business.user.entity.UserInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 查询加载处理器
 * 加载被 QueryProvide 标记的类，并按分组名称（即查询路径）生成 QueryHandleCache 查询缓存类
 * 当客户端访问 Query控制器的方法时，通过拦截器获取完整路径，并按规则分割获取到实际的方法路径（即分组名称）
 * 再通过分组名称获取到已经加载的 QueryHandleCache 查询缓存帮助类
 */
@Slf4j
public class QueryLoadProcessor {
    //查询缓存帮助  key = 分组名称 value = 查询缓存类
    private final static Map<String, QueryHandleCache<? extends IEntity>> HANDLE_CACHE_MAP = new HashMap<>(200);
    //当前访问路径（分组名称）
    private final static ThreadLocal<String> PATH_LOCAL = new ThreadLocal<>();
    //是否初始化
    private static boolean INIT_FLAG = false;

    /**
     * 在swagger 文档加载时,初始化缓存,并同时初始化动态文档
     */
    public static void init() {
        if (!INIT_FLAG) {
            synchronized (QueryLoadProcessor.class) {
                if (!INIT_FLAG) {
                    load();
                    //初始化动态文档
                    QueryDocumentProcessor.init(HANDLE_CACHE_MAP);
                    INIT_FLAG = true;
                }
            }
        }
    }

    /**
     * 根据分组名称获对应的查询缓存类
     *
     * @return
     */
    public static <T extends IEntity<T>> QueryHandleCache<T> getQueryHandle() {
        String groupName = PATH_LOCAL.get();
        Check.notNull(groupName, "无效的方法路径");
        QueryHandleCache cache = HANDLE_CACHE_MAP.get(groupName);
        if (cache.isNeedLogin()){
            SessionHelper.getUser();
        }
        Check.notNull(cache, "无效的查询方法[" + groupName + "]");
        return cache;
    }

    /**
     * 加载缓存
     */
    private static void load() {
        Set<Class<? extends IVo>> load = ClassUtils.load(Constant.ENTITY_PRE_FIX, IVo.class);
        if (load == null || load.size() == 0) {
            log.info("◕◕◕◕◕查询提供者:{}个", 0);
            return;
        }
        load.forEach(c -> {
            QueryProvide provide = c.getAnnotation(QueryProvide.class);
            if (provide != null) {
                addHandleCache(c, provide);
            }
            QueryProvides provides = c.getAnnotation(QueryProvides.class);
            if (provides != null) {
                addHandleCache(c, provides.value());
            }
            if (provide == null && provides == null) {
                return;
            }
            //读取字段
            List<Field> allFields = ClassUtils.getAllFields(c, QueryField.class, true);
            allFields.forEach(field -> {
                QueryField queryField = field.getAnnotation(QueryField.class);
                if (queryField != null) {
                    addHandleFieldCache(c, field, queryField);
                }
            });

            List<Field> moreFields = ClassUtils.getAllFields(c, QueryFields.class, true);
            moreFields.forEach(field -> {
                QueryFields queryFields = field.getAnnotation(QueryFields.class);
                if (queryFields != null) {
                    addHandleFieldCache(c, field, queryFields.value());
                }
            });
        });
        log.info("◕◕◕◕◕查询提供者:{}个", HANDLE_CACHE_MAP.size());
    }

    //添加分组查询字段
    private static <T extends IVo> void addHandleFieldCache(Class<T> entityType, Field field, QueryField... queryFields) {
        Set<String> keys = new HashSet<>();
        for (QueryField queryField : queryFields) {
            String queryGroup = QueryLoadProcessor.buildGroupName(entityType, queryField.queryGroup());//分组id
            if (StringUtils.isBlank(queryGroup)) {
                queryGroup = entityType.getSimpleName();
            }
            if (!keys.add(field.getName() + "-" + queryGroup)) {
                configError(entityType, "在[" + field.getName() + "]字段上配置了相同的分组[" + queryGroup + "]");
            }
            QueryHandleCache cache = HANDLE_CACHE_MAP.get(queryGroup);
            if (cache == null) {
                configError(entityType, "在[" + field.getName() + "]字段上配置了无效的分组名称[" + queryGroup + "]");
            }
            //增加特定描述
            if (!queryField.present().equals(QueryPresentCondition.NONE)) {
                noteHandle(cache, field, queryField.present().getDescribe());
            }
            if (StringUtils.isNotBlank(queryField.fixedValue())) {
                noteHandle(cache, field, queryField.fixedValue());
            }
            cache.getFields().add(new QueryHandleFieldCache(field, queryField));
        }
    }

    private static void noteHandle(QueryHandleCache cache, Field field, String describe) {
        StringBuilder noteBuilder = cache.getNoteBuilder();
        String fieldDescribe;
        ApiModelProperty ann = field.getAnnotation(ApiModelProperty.class);
        if (ann == null || StringUtils.isBlank(ann.value())) {
            fieldDescribe = field.getName();
        } else {
            fieldDescribe = ann.value();
        }
        if (noteBuilder == null) {
            noteBuilder = new StringBuilder();
            noteBuilder.append(cache.getDescribe());
            noteBuilder.append(":该方法仅返回");
            cache.setNoteBuilder(noteBuilder);
        } else {
            noteBuilder.append(",");
        }
        noteBuilder.append(fieldDescribe);
        noteBuilder.append("等于");
        noteBuilder.append(describe);
    }

    //添加查询分组
    private static <T extends IVo> void addHandleCache(Class<T> classType, QueryProvide... anns) {
        for (QueryProvide ann : anns) {
            if (ann.queryType().length == 0) {
                configError(classType, "未指定允许的查询类型");
            }
            //分组名称
            String groupName = buildGroupName(classType, ann.queryGroup());
            QueryHandleCache cache = null;
            try {
                cache = new QueryHandleCache(groupName, classType, ann);
            } catch (PlatformException e) {
                configError(classType, e.getMessage());
            }
            if (HANDLE_CACHE_MAP.containsKey(cache.getQueryGroup())) {
                configError(classType, "使用了相同的分组名称[" + cache.getQueryGroup() + "]");
            }
            HANDLE_CACHE_MAP.put(cache.getQueryGroup(), cache);
        }
    }

    private static void configError(Class<?> entityType, String message) {
        log.error("◕◕◕◕◕[" + entityType.getSimpleName() + "]类配置@QueryProvide时," + message + "请检查！！");
        System.exit(0);
    }

    public static void put(String path) {
        PATH_LOCAL.set(path);
    }

    public static void clear() {
        PATH_LOCAL.remove();
    }

    public static String buildGroupName(Class<?> entityType, String groupName) {
        return entityType.getName()
                .replace(Constant.ENTITY_PRE_FIX + ".", "")
                .replace(".vo", "")
                .replace(".", "-")
                .replace("Vo", "Query")
                + groupName;
//        return (entityType.getSimpleName() + groupName).toUpperCase();
    }
}
