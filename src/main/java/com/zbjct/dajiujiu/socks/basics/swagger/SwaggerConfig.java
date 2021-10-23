package com.zbjct.dajiujiu.socks.basics.swagger;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import com.github.xiaoymin.swaggerbootstrapui.model.SpringAddtionalModel;
import com.github.xiaoymin.swaggerbootstrapui.service.SpringAddtionalModelService;
import com.zbjct.dajiujiu.socks.basics.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;

/**
 * Swagger 文档配置
 */
@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
@Import(BeanValidatorPluginsConfiguration.class)
@Slf4j
public class SwaggerConfig {


    @Autowired
    private SpringAddtionalModelService springAddtionalModelService;

    @Bean
    public Docket demo(ServletContext servletContext) {
        SpringAddtionalModel springAddtionalModel = springAddtionalModelService
                .scan(Constant.ENTITY_PRE_FIX);
        Docket docket = new Docket(DocumentationType.SWAGGER_2).apiInfo(new ApiInfoBuilder()
                .title("左边牙齿疼-DaJiuJiu-Socks").description("左边牙齿疼-面向偷懒编程Demo").version("1.0").build())
                .groupName("Socks")
                .additionalModels(springAddtionalModel.getFirst(), springAddtionalModel.getRemaining())
                .select()
                .apis(RequestHandlerSelectors.basePackage(Constant.CONTROLLER_PRE_FIX))
                .paths(PathSelectors.any()).build();

        //增加报文头
//        ParameterBuilder ticketPar = new ParameterBuilder();
//        List<Parameter> pars = new ArrayList<Parameter>();
//        ticketPar.name("Access-Token").description("Authorization")
//                .modelRef(new ModelRef("string")).parameterType("header")
//                .required(false).build();
//        pars.add(ticketPar.build());
//        docket.globalOperationParameters(pars);

        return docket;
    }
}
