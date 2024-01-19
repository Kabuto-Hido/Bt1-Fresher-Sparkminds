package com.bt1.qltv1.config;

import com.bt1.qltv1.filter.MaintenanceModeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final MaintenanceModeInterceptor maintenanceModeInterceptor;

    public WebMvcConfig(MaintenanceModeInterceptor maintenanceModeInterceptor) {
        this.maintenanceModeInterceptor = maintenanceModeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(maintenanceModeInterceptor);
    }
}
