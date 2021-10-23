package com.zbjct.dajiujiu.socks.basics.validate;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidatorContext;

/**
 * 唯一值验证器
 */
public class UniqueValidator extends ExistValidator<Unique> {

    @Override
    public void initialize(Unique ann) {
        this.nullable = ann.nullable();
        this.message = StringUtils.isBlank(ann.message()) ?
                ann.value().getFieldDescribe() : ann.message();
        this.validateType = ann.value();
        this.inCompany = ann.inCompany();
    }

    @Override
    protected boolean pass(boolean exits, Object value, ConstraintValidatorContext context) {
        if (exits) {
            if (StringUtils.isBlank(errorMessage)) {
                this.errorMessage = this.message + "[" + value + "]已存在";
            }
            this.setTemplateMessage(errorMessage, context);
            return false;
        }
        return true;
    }
}
