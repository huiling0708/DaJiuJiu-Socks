package com.zbjct.dajiujiu.socks.basics.swagger;

import com.zbjct.dajiujiu.socks.basics.query.processor.QueryDocumentProcessor;
import com.zbjct.dajiujiu.socks.basics.query.processor.QueryLoadProcessor;
import org.springframework.stereotype.Component;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingScannerPlugin;
import springfox.documentation.spi.service.contexts.DocumentationContext;

import java.util.List;


/**
 * 自定义查询处理
 */
@Component
public class QueryCustomApiPlugin implements ApiListingScannerPlugin {
    @Override
    public List<ApiDescription> apply(DocumentationContext documentationContext) {
        //加载查询提供器
        QueryLoadProcessor.init();
        return QueryDocumentProcessor.getCacheList();
    }


    @Override
    public boolean supports(DocumentationType documentationType) {
        return DocumentationType.SWAGGER_2.equals(documentationType);
    }
}
