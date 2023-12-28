package com.bt1.qltv1;

import com.bt1.qltv1.config.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@ConfigurationPropertiesScan("com.bt1.qltv1.config")
public class Qltv1Application {

    public static void main(String[] args) {
        SpringApplication.run(Qltv1Application.class, args);

    }

}
