package com.bt1.qltv1.service;

import com.bt1.qltv1.dto.auth.RefreshTokenResponse;
import com.bt1.qltv1.entity.Session;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.repository.RoleRepository;
import com.bt1.qltv1.repository.SessionRepository;
import com.bt1.qltv1.repository.UserRepository;
import com.bt1.qltv1.util.ApplicationUser;
import com.bt1.qltv1.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Log4j
@Service
@Transactional
public class AuthService implements UserDetailsService {
    @Autowired
    private UserService userService;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findFirstByEmail(email);
        return new ApplicationUser(user);
    }

    public RefreshTokenResponse refreshToken(HttpServletRequest request) throws AuthenticationException {
        String authorizationHeader = request.getHeader("Authorization");
        //log header
        log.info("authorizationHeader: " + authorizationHeader);
        String refreshToken = null;
        String email = null;

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new AuthenticationException("Wrong authorization header");
        }
        refreshToken = authorizationHeader.substring(7);
        try {
            email = jwtUtil.extractUsername(refreshToken);
        } catch (JwtException ex) {
            throw new AuthenticationException(ex.getMessage());
        }

        UserDetails userDetails = loadUserByUsername(email);
        jwtUtil.validateToken(refreshToken,userDetails);

        String jti = jwtUtil.extractJTi(refreshToken);
        //generate new access token
        String accessToken = jwtUtil.generateToken(userDetails.getUsername(), jti);

        RefreshTokenResponse response = RefreshTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken).build();
        //log RefreshTokenResponse
        log.info("RefreshTokenResponse: " + response.toString());
        return response;
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
