package com.zbjct.dajiujiu.socks.basics.validate;


import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidatorContext;

/**
 * 有效验证器
 */
public class EffectiveValidator extends ExistValidator<Effective> {


    @Override
    public void initialize(Effective ann) {
        this.nullable = ann.nullable();
        this.message = StringUtils.isBlank(ann.message()) ?
                ann.value().getFieldDescribe() : ann.message();
        this.validateType = ann.value();
        this.inCompany = ann.inCompany();
    }

    @Override
    protected boolean pass(boolean exits, Object value, ConstraintValidatorContext context) {
        if (!exits) {
            if (StringUtils.isBlank(errorMessage)) {
                this.errorMessage = "无效的" + this.message + "[" + value + "]";
            }
            this.setTemplateMessage(errorMessage, context);
            return false;
        }
        return true;
    }
}
