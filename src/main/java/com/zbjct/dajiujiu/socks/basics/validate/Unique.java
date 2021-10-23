package com.zbjct.dajiujiu.socks.basics.validate;



import com.zbjct.dajiujiu.socks.basics.validate.param.ValidateType;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 *  唯一值验证注解
 *  验证传入字段值在指定实体表中的数据是否是唯一的
 */
@Constraint(validatedBy = {UniqueValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Unique.List.class)
public @interface Unique {

    ValidateType value();//验证映射

    boolean nullable() default false;//是否允许为空

    boolean inCompany() default false;//验证公司中是否唯一 即查询时拼接 companyId

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ ElementType.METHOD, ElementType.FIELD })
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        Unique[] value();
    }
}
