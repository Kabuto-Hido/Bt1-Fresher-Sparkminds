package com.bt1.qltv1.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.data.domain.AuditorAware;

import java.util.Objects;
import java.util.Optional;

@Log4j
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        String email = UserDetailsServiceImpl.getEmailLoggedIn();
        log.info(email);
        if(Objects.requireNonNull(email).equals("anonymousUser")){
            return Optional.of("System");
        }
        return Optional.of(email);
    }
}
