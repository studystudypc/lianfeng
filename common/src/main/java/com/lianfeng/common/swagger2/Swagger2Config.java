package com.lianfeng.common.swagger2;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import com.lianfeng.common.swagger2.Swagger2ConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @version 1.8
 * @注释 接口文档配置类
 * @Author liuchuanping
 * @Date 2024-11-13 15:09
 */

@SpringBootConfiguration
@EnableSwagger2
@EnableSwaggerBootstrapUI
@Slf4j
public class Swagger2Config {

    @Autowired
    private Swagger2ConfigProperties swagger2ConfigProperties;

    @Bean
    public Docket panServerApi() {
        //创建一个Docket对象，用于Swagger 2的API文档生成。
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                //根据properties.isShow()的值来启用或禁用Swagger功能。
                .enable(swagger2ConfigProperties.isShow())
                //设置API文档的组名，可以用于对不同的API进行分组。
                .groupName(swagger2ConfigProperties.getGroupName())
                //提供API文档的基本信息，如标题、描述、版本等。apiInfo()是一个方法，返回ApiInfo对象。
                .apiInfo(apiInfo())
                //禁用Swagger的默认响应消息，允许自定义响应。
                .useDefaultResponseMessages(false)
                //初始化用于构建API选择器的构建器。
                .select()
                //指定要扫描的基础包，只生成该包下Controller的API文档。
                .apis(RequestHandlerSelectors.basePackage(swagger2ConfigProperties.getBasePackage()))
                //选择所有路径进行文档生成。
                .paths(PathSelectors.any())
                .build();
        log.info("The swagger2 have been loaded successfully!");
        return docket;
    }

    private ApiInfo apiInfo() {
//        创建一个ApiInfoBuilder对象，用于构建ApiInfo实例。
        return new ApiInfoBuilder()
//        设置API文档的标题，值来自properties.getTitle()。
                .title(swagger2ConfigProperties.getTitle())
//                设置API文档的描述信息，值来自properties.getDescription()。
                .description(swagger2ConfigProperties.getDescription())
//                设置服务条款URL，值来自properties.getTermsOfServiceUrl()
                .termsOfServiceUrl(swagger2ConfigProperties.getTermsOfServiceUrl())
//                设置联系信息，包括姓名、URL和邮箱，值分别来自properties.getContactName()、properties.getContactUrl()、properties.getContactEmail()。
                .contact(new Contact(swagger2ConfigProperties.getContactName(), swagger2ConfigProperties.getContactUrl(), swagger2ConfigProperties.getContactName()))
//                设置API文档的版本信息，值来自properties.getVersion()。
                .version(swagger2ConfigProperties.getVersion())
                .build();
    }
}
