package com.zbjct.dajiujiu.socks.basics.swagger;

import com.fasterxml.classmate.TypeResolver;
import com.zbjct.dajiujiu.socks.basics.query.processor.QueryDocumentProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;

/**
 * 自定义参数处理
 */
@Component
public class QueryParamApiReader implements ParameterBuilderPlugin {

    @Autowired
    private TypeResolver typeResolver;


    @Override
    public void apply(ParameterContext parameterContext) {
        QueryDocumentProcessor.initParam(typeResolver, parameterContext);
    }


    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }
}
