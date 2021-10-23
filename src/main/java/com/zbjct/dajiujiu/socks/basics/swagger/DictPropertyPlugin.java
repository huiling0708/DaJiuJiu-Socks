package com.zbjct.dajiujiu.socks.basics.swagger;

import com.zbjct.dajiujiu.socks.basics.dict.base.IDict;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ModelPropertyBuilder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import java.lang.reflect.Field;

/**
 * 数据字典处理
 */
@Component
public class DictPropertyPlugin implements ModelPropertyBuilderPlugin {

    @Override
    public void apply(ModelPropertyContext context) {
        if (context.getBeanPropertyDefinition().isPresent()) {
            Class<?> rawPrimaryType = context.getBeanPropertyDefinition().get().getRawPrimaryType();
            //字典类型
            if (IDict.class.isAssignableFrom(rawPrimaryType)) {
                String description;
                try {
                    Field mField = ModelPropertyBuilder.class.getDeclaredField("description");
                    mField.setAccessible(true);
                    description = mField.get(context.getBuilder()) + "(字典:" + rawPrimaryType.getSimpleName() + ")";
                } catch (Exception e) {
                    return;
                }
                context.getBuilder().description(description);
            }
        }
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }
}
