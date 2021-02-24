package com.herocheer.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

/**
 * @author chenwf
 * @date 2020/12/14
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Value("${swagger.enable}")
    private Boolean enable;
    @Value("${swagger.basePackage}")
    private String basePackage;
    @Value("${swagger.title}")
    private String title;
    @Value("${swagger.description}")
    private String description;
    @Value("${swagger.version}")
    private String version;
    @Value("${swagger.termsOfServiceUrl}")
    private String termsOfServiceUrl;
    @Value("${swagger.license}")
    private String license;
    @Value("${swagger.licenseUrl}")
    private String licenseUrl;
    @Value("${swagger.contact.name}")
    private String name;
    @Value("${swagger.contact.url}")
    private String url;
    @Value("${swagger.contact.email}")
    private String email;
    @Value("${swagger.host}")
    private String host;

    @Bean
    public Docket docketUac() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(enable)
                .host(host)
                .select()// 通过.select()方法，去配置扫描接口,RequestHandlerSelectors配置如何扫描接口
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .build();
    }

    //配置文档信息
    private ApiInfo apiInfo() {
        Contact contact = new Contact(name, url, email);
        return new ApiInfo(
                title, // 标题
                description, // 描述
                version, // 版本
                termsOfServiceUrl, // 组织链接
                contact, // 联系人信息
                license, // 许可
                licenseUrl, // 许可连接
                new ArrayList<>()// 扩展
        );
    }
}
