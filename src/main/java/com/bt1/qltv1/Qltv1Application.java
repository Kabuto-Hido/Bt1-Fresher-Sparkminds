package com.bt1.qltv1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.bt1.qltv1.config")
public class Qltv1Application {

    public static void main(String[] args) {
        SpringApplication.run(Qltv1Application.class, args);
    }
}
