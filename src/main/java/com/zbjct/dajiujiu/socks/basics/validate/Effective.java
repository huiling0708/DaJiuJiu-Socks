package com.zbjct.dajiujiu.socks.basics.validate;


import com.zbjct.dajiujiu.socks.basics.validate.param.ValidateType;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *   有效验证注解
 *   验证传入字段值在指定实体表中是有效的
 *   当checkCompany =true 时，指该数据在当前登录公司下是有效的
 */
@Constraint(validatedBy = {EffectiveValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Effective {

    ValidateType value();//验证映射

    boolean nullable() default false;//是否允许为空

    boolean inCompany() default false;//验证公司中是否有效 即查询时拼接 companyId

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
