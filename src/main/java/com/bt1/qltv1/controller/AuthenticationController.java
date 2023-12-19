package com.bt1.qltv1.controller;

import com.bt1.qltv1.dto.auth.LoginRequest;
import com.bt1.qltv1.dto.auth.LoginResponse;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.service.AuthService;
import com.bt1.qltv1.service.UserService;
import com.bt1.qltv1.util.JwtUtil;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;

@Log4j
@RestController
@CrossOrigin
@RequestMapping("/api/v1")
public class AuthenticationController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public Object authenticate(@RequestBody LoginRequest loginRequest) throws Exception {
        //userService.save("admin@gmail.com","admin");
        //log login request
        log.info("Login request: "+ loginRequest.toString());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken
                            (loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException exception) {
            log.warn("login fail: Email or password is wrong");
            return ResponseEntity.badRequest().body("Email or password is invalid");
        }
        final UserDetails userDetails = authService
                .loadUserByUsername(loginRequest.getEmail());

        //check valid email
        User user = userService.findFirstByEmail(loginRequest.getEmail());
        //check status account
        if(!user.isActive()){
            throw new AuthenticationException("Your account is "+ user.getStatus());
        }
        //generate token
        final String accessToken = jwtUtil.generateToken(userDetails.getUsername());
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        LoginResponse loginResponse = LoginResponse
                .builder().accessToken(accessToken).refreshToken(refreshToken)
                .username(loginRequest.getEmail()).id(user.getId())
                .role((Set<GrantedAuthority>) userDetails.getAuthorities()).build();
        //log response
        log.info("Login response : "+ loginResponse.toString());

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh-token")
    public Object refreshToken(HttpServletRequest request) throws AuthenticationException {
        log.info("Exact refresh token!");
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/register")
    public ResponseEntity<?> saveUser() {
        userService.save("a@gmail.com","123456789");
        return ResponseEntity.ok(
                "Create account successful! Please check your email to activated your account!");

    }
}
