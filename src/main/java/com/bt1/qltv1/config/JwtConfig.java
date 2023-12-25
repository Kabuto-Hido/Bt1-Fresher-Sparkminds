package com.bt1.qltv1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;


@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "application.jwt")
@Data
public class JwtConfig {
    private String secretKey;
    private String tokenPrefix;
    private Integer refreshTokenExpirationAfterDays;
    private Integer tokenExpirationAfterDays;
    private Integer tokenExpirationAfterMinutes;
}
