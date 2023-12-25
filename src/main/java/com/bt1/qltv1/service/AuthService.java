package com.bt1.qltv1.service;

import com.bt1.qltv1.dto.auth.RefreshTokenRequest;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private final UserService userService;
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findFirstByEmail(email);
        return new ApplicationUser(user);
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request){
        //log header
        String refreshToken = request.getRefreshToken();
        String email = null;

        try {
            email = jwtUtil.extractUsername(refreshToken);
        } catch (JwtException ex) {
            throw new JwtException(ex.getMessage());
        }

        if (Boolean.TRUE.equals(jwtUtil.isTokenExpired(refreshToken))) {
            throw new JwtException("Your token is expired. Please login again.");
        }

        String jti = jwtUtil.extractJTi(refreshToken);
        if (sessionService.checkIsBlockSession(jti)) {
            throw new JwtException("Your refresh token can not use any more.");
        }
        //generate new access token
        String accessToken = jwtUtil.generateToken(email, jti);
        log.info("access token: "+accessToken);

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
