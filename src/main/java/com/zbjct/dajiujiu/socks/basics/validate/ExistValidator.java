package com.zbjct.dajiujiu.socks.basics.validate;

import com.zbjct.dajiujiu.socks.basics.database.impl.JpaWrapper;
import com.zbjct.dajiujiu.socks.basics.validate.param.ValidateContent;
import com.zbjct.dajiujiu.socks.basics.validate.param.ValidateType;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;

/**
 * 存在验证器
 *
 * @param <A>
 */
public abstract class ExistValidator<A extends Annotation> implements ConstraintValidator<A, Object> {

    protected boolean nullable;
    protected String message;
    protected ValidateType validateType;
    protected boolean inCompany;
    protected String errorMessage;

    public abstract void initialize(A ann);

    @SneakyThrows
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null ||
                ((value instanceof String) && StringUtils.isBlank((String) value))) {
            if (this.nullable) {
                return true;
            } else {
                this.setTemplateMessage(message + "不能为空", context);
                return false;
            }
        }
        if (value instanceof ValidateContent) {
            ValidateContent param = (ValidateContent) value;
            this.validateType = param.getValidateType();
            this.inCompany = param.getInCompany().isBooleanValue();
            this.message = param.getValidateType().getFieldDescribe();
            this.errorMessage = param.getErrorMessage();
            value = param.getValue();
        } else {
            this.errorMessage = null;
        }
        JpaWrapper jpaWrapper = JpaWrapper.create(validateType.getEntityClassType());
        jpaWrapper.where(validateType.getField().getField(), value);
        if (inCompany) {
            jpaWrapper.presentCompany();
        }
        return this.pass(jpaWrapper.doCheckExists(), value, context);
    }

    protected abstract boolean pass(boolean exits, Object value, ConstraintValidatorContext context);

    protected final void setTemplateMessage(String mes, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(mes).addConstraintViolation();
    }
}
