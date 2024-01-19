package com.bt1.qltv1.config;

//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.ApiKey;
//import springfox.documentation.service.AuthorizationScope;
//import springfox.documentation.service.SecurityReference;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spi.service.contexts.SecurityContext;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//import java.util.Collections;
//import java.util.List;
//
//@Configuration
//@EnableSwagger2
//public class SwaggerConfig {
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title("REST API")
//                .description("Library management services")
//                .build();
//    }
//
//    private ApiKey apiKey(){
//        return new ApiKey("JWT", "Authorization","header");
//    }
//
//    private List<SecurityContext> securityContexts(){
//        return Collections.singletonList(SecurityContext.builder()
//                .securityReferences(securityReferences()).build());
//    }
//
//    private List<SecurityReference> securityReferences(){
//        AuthorizationScope scope = new AuthorizationScope("global","accessEverything");
//        return Collections.singletonList(new SecurityReference("JWT",
//                new AuthorizationScope[]{scope}));
//    }
//
//    @Bean
//    public Docket matchApi() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.bt1.qltv1"))
//                .paths(PathSelectors.any())
//                .build()
//                .apiInfo(apiInfo())
//                .securityContexts(securityContexts())
//                .securitySchemes(Collections.singletonList(apiKey()));
//    }
//}
