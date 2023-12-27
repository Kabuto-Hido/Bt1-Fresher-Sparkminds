package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.service.UserService;
import com.bt1.qltv1.util.ApplicationUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
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
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findFirstByEmail(email);
        return new ApplicationUser(user);
    }

    public static String GetEmailLoggedIn() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;

        if (principal instanceof ApplicationUser) {
            email = ((ApplicationUser) principal).getUsername();
        } else {
            email = principal.toString();
        }

        return email;
    }


}
