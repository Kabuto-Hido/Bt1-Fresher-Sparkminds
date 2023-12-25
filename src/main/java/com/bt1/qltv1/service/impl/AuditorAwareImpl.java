package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.service.AuthService;
import lombok.extern.log4j.Log4j;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Log4j
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        String email = AuthService.GetEmailLoggedIn();
        log.info(email);
        return Optional.ofNullable(email).filter(s -> !s.isEmpty());
    }
}