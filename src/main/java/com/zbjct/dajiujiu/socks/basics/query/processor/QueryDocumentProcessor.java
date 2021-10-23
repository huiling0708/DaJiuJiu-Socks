package com.zbjct.dajiujiu.socks.basics.query.processor;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.Sets;
import com.zbjct.dajiujiu.socks.basics.database.define.IEntity;
import com.zbjct.dajiujiu.socks.basics.database.define.constant.SqlExpression;
import com.zbjct.dajiujiu.socks.basics.dict.base.IDict;
import com.zbjct.dajiujiu.socks.basics.query.QueryField;
import com.zbjct.dajiujiu.socks.basics.query.QueryPresentCondition;
import com.zbjct.dajiujiu.socks.basics.query.QueryType;
import io.swagger.annotations.ApiModelProperty;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.Operation;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文档处理器
 * 因为通过@QueryProvide 生成的查询方法，并不会真正的生成控制器
 * 所以通过动态生成类，并添加到Swagger ApiDescription 中
 */
@Slf4j
public class QueryDocumentProcessor {

    //swagger api文档列表
    private static List<ApiDescription> CACHE_LIST = new ArrayList<>(100);
    //查询参数类缓存
    private static Map<String, Class<?>> QUERY_PARAM_CLASS_TYPE_CACHE = new HashMap<>(150);
    //动态类 列表
    private static Set<CtClass> QUERY_PARAM_CT_CLASS_TYPE_CACHE = new HashSet<>(150);
    //存放动态类的包
    private final static String QUERY_PARAM_CLASS_PACKAGE =
            QueryDocumentProcessor.class.getPackage().getName() + ".param.";
    //是否已经初始化动态参数
    private static boolean INIT_PARAM = false;

    //获取 api文档列表
    public static List<ApiDescription> getCacheList() {
        return CACHE_LIST;
    }

    /**
     * 初始化文档
     * 当 QueryHandleCache 被加载结束之后，为每一个Cache 生成动态参数类，并组合成swagger api文档列表
     *
     * @param map
     */
    protected static void init(Map<String, QueryHandleCache<? extends IEntity>> map) {
        List<QueryHandleCache> list = map.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
        list.forEach(cache -> {
            cache.getQueryType().forEach(queryType -> {
                CACHE_LIST.add(buildApiDescription(cache, (QueryType) queryType));
            });
        });
    }

    /**
     * 初始化 动态参数
     *
     * @param typeResolver
     * @param parameterContext
     */
    public static void initParam(TypeResolver typeResolver, ParameterContext parameterContext) {
        if (!INIT_PARAM) {
            synchronized (QueryDocumentProcessor.class) {
                if (!INIT_PARAM) {
                    for (Map.Entry<String, Class<?>> stringClassEntry : QUERY_PARAM_CLASS_TYPE_CACHE.entrySet()) {
                        initParam(stringClassEntry.getValue(), typeResolver, parameterContext);
                    }
                    QUERY_PARAM_CT_CLASS_TYPE_CACHE.stream().forEach(i -> i.detach());
                    INIT_PARAM = true;
                }
            }
        }
    }

    //初始化动态参数
    private static void initParam(Class<?> aClass, TypeResolver typeResolver, ParameterContext parameterContext) {
        parameterContext.getDocumentationContext().
                getAdditionalModels().add(typeResolver.resolve(aClass));
        parameterContext.parameterBuilder()  //修改Map参数的ModelRef为我们动态生成的class
                .parameterType("body")
                .modelRef(new ModelRef(aClass.getSimpleName()))
                .name(aClass.getSimpleName());
    }

    /**
     * 生成单个api 文档
     *
     * @param cache
     * @param queryType
     * @return
     */
    private static ApiDescription buildApiDescription(QueryHandleCache cache, QueryType queryType) {
        String groupName = "common";
        String path = "/query/" + queryType.name().toLowerCase() + "/" + cache.getQueryGroup();
        String description = cache.getDescribe();
        List<Operation> operations = new ArrayList<>();
        String tags = cache.getTags();
        if (StringUtils.isBlank(tags)) {
            tags = queryType.getDescribe() + "-" + queryType.name();
        }
        //基础
        OperationBuilder operationBuilder = new OperationBuilder(new CachingOperationNameGenerator())
                .method(HttpMethod.POST)
                .produces(Sets.newHashSet(MediaType.APPLICATION_JSON_VALUE))
                .consumes(Sets.newHashSet(MediaType.APPLICATION_JSON_VALUE))
                .summary(cache.getDescribe())
                .uniqueId(cache.getQueryGroup() + "_" + queryType.name())
                .tags(Sets.newHashSet(tags));//归类标签
        //处理方法描述
        if (cache.getNoteBuilder() == null) {
            operationBuilder.notes(cache.getDescribe());
        } else {
            cache.getNoteBuilder().append("的结果集");
            operationBuilder.notes(cache.getNoteBuilder().toString());
        }
        //参数
        operationBuilder.parameters(Collections.singletonList(handleParam(cache, queryType)));

        //返回值
        ResponseMessage responseMessage = new ResponseMessageBuilder().
                code(200).message("OK").
                responseModel(new ModelRef(cache.getResultType().getSimpleName())).build();
        operationBuilder.responseMessages(Collections.singleton(responseMessage));
        operations.add(operationBuilder.build());
        return new ApiDescription(groupName, path, description, operations, false);
    }

    /**
     * 生成 单个参数类
     *
     * @param cache
     * @param queryType
     * @return
     */
    private static Parameter handleParam(QueryHandleCache cache, QueryType queryType) {
        ParameterBuilder parameterBuilder = new ParameterBuilder();
        parameterBuilder.description("查询参数");//参数描述
        parameterBuilder.required(true);//是否必填

        Class<?> aClass = QueryType.PAGE.equals(queryType) ?
                buildPageQueryParamCtClass(cache) :
                getQueryParamClass(cache);

        parameterBuilder.type(new TypeResolver().resolve(aClass));//参数数据类型
        parameterBuilder.modelRef(new ModelRef(aClass.getSimpleName())); //参数数据类型
        parameterBuilder.name("params");//参数名称
        parameterBuilder.parameterType("body");//参数类型
        parameterBuilder.parameterAccess("access");
        return parameterBuilder.build();
    }

    /**
     * 获取查询参数类
     *
     * @param cache
     * @return
     */
    @SneakyThrows
    private static Class<?> getQueryParamClass(QueryHandleCache cache) {
        Class<?> aClass = QUERY_PARAM_CLASS_TYPE_CACHE.get(cache.getQueryGroup());
        if (aClass != null) {
            return aClass;
        }
        CtClass ctClass = buildQueryParamCtClass(cache);
        aClass = ctClass.toClass();
        QUERY_PARAM_CLASS_TYPE_CACHE.put(cache.getQueryGroup(), aClass);
        QUERY_PARAM_CT_CLASS_TYPE_CACHE.add(ctClass);
        return aClass;
    }

    /**
     * 生成查询参数的动态类
     */
    private static CtClass buildQueryParamCtClass(QueryHandleCache cache) {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass(QUERY_PARAM_CLASS_PACKAGE + cache.getQueryGroup());
        List<QueryHandleFieldCache> fields = cache.getFields();
        fields.forEach(f -> {
            if (!f.getQueryField().present().equals(QueryPresentCondition.NONE)) {
                return;//指定当前条件后不在查询条件中显示
            }
            if (StringUtils.isNotBlank(f.getQueryField().fixedValue())) {
                return;//指定固定值后 不在查询条件中显示
            }
            try {
                ctClass.addField(buildQueryParamField(ctClass, f));
            } catch (CannotCompileException e) {
                log.error(e.getReason());
            }
        });
        return ctClass;
    }

    /**
     * 创建复合查询条件动态类
     *
     * @param fieldType
     * @return
     */
    @SneakyThrows
    private static Class<?> buildCompoundConditionCtClass(Class<?> fieldType,SqlExpression[] condition) {
        String className = "CompoundCondition" + fieldType.getSimpleName();
        ClassPool pool = ClassPool.getDefault();
        Class<?> aClass = QUERY_PARAM_CLASS_TYPE_CACHE.get(className);
        if (aClass != null) {
            return aClass;
        }
        CtClass ctClass = pool.makeClass(QUERY_PARAM_CLASS_PACKAGE + className);
        CtField startCtClass = buildQueryParamField(ctClass, fieldType, QueryExecuteProcessor.COMPOUND_CONDITION_START,
                "查询开始条件"+condition[0].getValue(), true);
        CtField endCtClass = buildQueryParamField(ctClass, fieldType, QueryExecuteProcessor.COMPOUND_CONDITION_END,
                "查询结束条件"+condition[1].getValue(), true);
        ctClass.addField(startCtClass);
        ctClass.addField(endCtClass);
        aClass = ctClass.toClass();
        //加入缓存
        QUERY_PARAM_CLASS_TYPE_CACHE.put(className, aClass);
        QUERY_PARAM_CT_CLASS_TYPE_CACHE.add(ctClass);
        return aClass;
    }

    /**
     * 生成分页查询参数类
     *
     * @param cache
     * @return
     */
    @SneakyThrows
    private static Class<?> buildPageQueryParamCtClass(QueryHandleCache cache) {
        String className = "QueryParam" + cache.getQueryGroup();
        ClassPool pool = ClassPool.getDefault();
        Class<?> aClass = QUERY_PARAM_CLASS_TYPE_CACHE.get(className);
        if (aClass != null) {
            return aClass;
        }
        CtClass ctClass = pool.makeClass(QUERY_PARAM_CLASS_PACKAGE + className);
        CtField numberCtClass = buildQueryParamField(ctClass, int.class, "number",
                "页数 默认第1页", false);
        CtField sizeCtClass = buildQueryParamField(ctClass, int.class, "size",
                "每页显示条数 默认20条", false);
        Class<?> queryParamClass = getQueryParamClass(cache);//正在的查询条件
        CtField ctQueryParamClass = buildQueryParamField(ctClass, queryParamClass, "queryParams",
                "查询参数", true);
        ctClass.addField(numberCtClass);
        ctClass.addField(sizeCtClass);
        ctClass.addField(ctQueryParamClass);
        aClass = ctClass.toClass();
        //加入缓存
        QUERY_PARAM_CLASS_TYPE_CACHE.put(className, aClass);
        QUERY_PARAM_CT_CLASS_TYPE_CACHE.add(ctClass);
        return aClass;
    }

    /**
     * 生成动态查询查询字段
     *
     * @param ctClass
     * @param cache
     * @return
     */
    @SneakyThrows
    private static CtField buildQueryParamField(CtClass ctClass, QueryHandleFieldCache cache) {
        QueryField queryField = cache.getQueryField();
        Field field = cache.getField();
        //获取属性类型CtClass
        //设置 ApiModelProperty 注解
        ApiModelProperty ap = field.getAnnotation(ApiModelProperty.class);
        String description = field.getName();
        if (ap != null) {
            description = ap.value();
        }
        Class<?> fieldType = field.getType();
        SqlExpression firstCondition = queryField.condition()[0];
        //多个条件情况处理
        if (queryField.condition().length == 2) {
            fieldType = buildCompoundConditionCtClass(fieldType,queryField.condition());
        } else if (SqlExpression.IN.equals(firstCondition)) {
            if (IDict.class.isAssignableFrom(fieldType)) {
                description += ("(字典:" + fieldType.getSimpleName() + ")");
            }
            fieldType = List.class;
        }

        if (SqlExpression.LIKE.equals(firstCondition)
                || SqlExpression.LIKE_LEFT.equals(firstCondition)
                || SqlExpression.LIKE_RIGHT.equals(firstCondition)) {
            description += (" (模糊查询)");
        }

        return buildQueryParamField(ctClass, fieldType, field.getName(), description, queryField.mustInput());
    }

    /**
     * 生成单个查询参数字段
     *
     * @param ctClass
     * @param fieldType
     * @param fieldName
     * @param description
     * @param mustInput
     * @return
     */
    @SneakyThrows
    private static CtField buildQueryParamField(CtClass ctClass, Class<?> fieldType,
                                                String fieldName, String description,
                                                boolean mustInput) {

        ClassPool pool = ClassPool.getDefault();
        //枚举类时
        if (IDict.class.isAssignableFrom(fieldType)) {
            Class<?> aClass1 = Thread.currentThread().getContextClassLoader().
                    loadClass(fieldType.getName());
            ClassClassPath classPath = new ClassClassPath(aClass1);
            pool.insertClassPath(classPath);
        }
        //获取属性类型CtClass
        CtClass fieldCtType = pool.get(fieldType.getName());
        CtField ctField = new CtField(fieldCtType, fieldName, ctClass);
        ctField.setModifiers(Modifier.PUBLIC);
        //设置 ApiModelProperty 注解
        ConstPool constPool = ctClass.getClassFile().getConstPool();
        AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation ann = new Annotation("io.swagger.annotations.ApiModelProperty", constPool);
        //示例
        if (Number.class.isAssignableFrom(fieldType)) {
            ann.addMemberValue("example", new StringMemberValue("0", constPool));
        } else if (BigDecimal.class.isAssignableFrom(fieldType)) {
            ann.addMemberValue("example", new StringMemberValue("0.00", constPool));
        } else if (IDict.class.isAssignableFrom(fieldType)) {
            //字典
            Class<? extends Enum> dict = (Class<? extends Enum>) fieldType;
            ann.addMemberValue("example", new StringMemberValue(dict.getEnumConstants()[0].name(), constPool));
            description += ("(字典:" + fieldType.getSimpleName() + ")");
        } else {
            //ann.addMemberValue("example", new StringMemberValue(fieldType.getSimpleName(), constPool));
        }
        //描述
        ann.addMemberValue("value", new StringMemberValue(description, constPool));
        ann.addMemberValue("required", new BooleanMemberValue(mustInput, constPool));

        attr.addAnnotation(ann);
        ctField.getFieldInfo().addAttribute(attr);
        return ctField;
    }
}
