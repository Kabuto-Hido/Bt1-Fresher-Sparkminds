package com.bt1.qltv1.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Log4j
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        String email = UserDetailsServiceImpl.getEmailLoggedIn();
        log.info(email);
        return Optional.of(Optional.ofNullable(email)
                .orElse("System"));
    }
}
