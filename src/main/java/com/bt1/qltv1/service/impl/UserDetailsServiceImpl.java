package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.repository.UserRepository;
import com.bt1.qltv1.service.UserService;
import com.bt1.qltv1.util.ApplicationUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(()->
                new NotFoundException("User with email " + email + " was not found in the database",
                        "{user.login.email.invalid}"));
        return new ApplicationUser(user);
    }

    public static String getEmailLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return null;
        }
        if (authentication.getPrincipal() instanceof ApplicationUser) {
            return ((ApplicationUser) authentication.getPrincipal()).getUsername();
        }
        if (authentication.getPrincipal() instanceof String) {
            return authentication.getPrincipal().toString();
        }
        return null;
    }


}
