package com.bt1.qltv1.service;

import com.bt1.qltv1.dto.auth.LoginRequest;
import com.bt1.qltv1.dto.auth.LoginResponse;
import com.bt1.qltv1.dto.auth.RefreshTokenRequest;
import com.bt1.qltv1.dto.auth.RefreshTokenResponse;
import com.bt1.qltv1.entity.Session;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.enumeration.SessionStatus;
import com.bt1.qltv1.exception.BadRequest;
import com.bt1.qltv1.exception.MfaException;
import com.bt1.qltv1.service.impl.UserDetailsServiceImpl;
import com.bt1.qltv1.util.Global;
import com.bt1.qltv1.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@Log4j
@RequiredArgsConstructor
public class AuthService {
    private final SessionService sessionService;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final MfaService mfaService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;

    public LoginResponse login(LoginRequest loginRequest){
        //log login request
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken
                            (loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException exception) {
            throw new BadRequest("Email or password is invalid",
                    "user.email-password.wrong");
        }
        final UserDetails userDetails = userDetailsService.
                loadUserByUsername(loginRequest.getEmail());

        //check valid email
        User user = userService.findFirstByEmail(loginRequest.getEmail());

        //check is account enable MFA
        if(user.isMfaEnabled() && (!mfaService.verifyOtp(user.getSecret(),
                loginRequest.getCode()))){
            throw new MfaException("Invalid MFA code","mfa.code.invalid");
        }

        //generate token
        String idToken = Global.randomNumber();

        final String accessToken = jwtUtil.generateToken(userDetails.getUsername(), idToken);
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername(), idToken);

        LocalDateTime expiredTime = jwtUtil.extractExpiration(refreshToken);

        Session newSession = new Session();
        newSession.setExpiredDate(expiredTime);
        newSession.setJti(idToken);
        newSession.setStatus(SessionStatus.ACTIVE);

        //save session to db
        sessionService.saveSession(newSession, user.getId());

        LoginResponse loginResponse = LoginResponse
                .builder().accessToken(accessToken).refreshToken(refreshToken)
                .username(loginRequest.getEmail()).id(user.getId())
                .role((Set<GrantedAuthority>) userDetails.getAuthorities()).build();

        log.info(loginResponse);

        return loginResponse;
    }

    public void logout(HttpServletRequest request){
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new JwtException("Invalid token!!");
        }
        String jwt = authorizationHeader.substring(7);
        sessionService.blockSession(jwtUtil.extractJTi(jwt));
    }

    //INHERITANCE FOR ADMIN

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request){
        String refreshToken = request.getRefreshToken();
        String email;

        try {
            email = jwtUtil.extractUsername(refreshToken);
        } catch (JwtException ex) {
            throw new JwtException(ex.getMessage());
        }

        if (Boolean.TRUE.equals(jwtUtil.isTokenExpired(refreshToken))) {
            throw new JwtException("Your token is expired. Please login again.");
        }

        String jti = jwtUtil.extractJTi(refreshToken);
        LocalDateTime expiredTime = jwtUtil.extractExpiration(refreshToken);

        if(!sessionService.checkIsRightRefreshToken(jti, expiredTime)){
            throw new JwtException("Please use right refresh token");
        }
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


}
