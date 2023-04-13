package org.dromara.forest.example.springboot;

import com.dtflys.forest.springboot.annotation.ForestScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@ForestScan(basePackages = "com.dtflys.forest.example.client")
public class ForestExampleApplication {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dtflys.forest.example.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Forest示例工程中的Demo接口")
                .contact(new Contact("DtFlys", "https://gitee.com/dt_flys", "dt_flys@hotmail.com"))
                .version("1.0")
                .description("Forest示例工程中的Demo接口")
                .build();
    }



    public static void main(String[] args) {
        try {
            SpringApplication.run(ForestExampleApplication.class, args);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }
}
