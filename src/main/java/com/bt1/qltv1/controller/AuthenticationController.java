package com.bt1.qltv1.controller;

import com.bt1.qltv1.util.Global;
import com.bt1.qltv1.dto.auth.*;
import com.bt1.qltv1.enumeration.SessionStatus;
import com.bt1.qltv1.entity.Session;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.service.AuthService;
import com.bt1.qltv1.service.MfaService;
import com.bt1.qltv1.service.SessionService;
import com.bt1.qltv1.service.UserService;
import com.bt1.qltv1.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/v1")
public class AuthenticationController {
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final SessionService sessionService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final MfaService mfaService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginRequest loginRequest) throws Exception {
        //log login request
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken
                            (loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException exception) {
            throw  new IllegalArgumentException("Email or password is invalid");
            //return ResponseEntity.badRequest().body("Email or password is invalid");
        }
        final UserDetails userDetails = authService
                .loadUserByUsername(loginRequest.getEmail());

        //check valid email
        User user = userService.findFirstByEmail(loginRequest.getEmail());
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

        //log response
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request){
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @GetMapping("/generate-mfa")
    public ResponseEntity<MfaResponse> generateMfa(){
        String email = AuthService.GetEmailLoggedIn();
        return ResponseEntity.ok(mfaService.generateSecretKeyAndQrcode(email));
    }

    @PostMapping("/enable-mfa")
    public ResponseEntity<String> enableMfa(@NotNull @RequestParam String secret){
        if(userService.enableMfa(secret)>0){
            return ResponseEntity.ok("Enable MFA success!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found or update failed.");
    }



    @PostMapping("/register")
    public ResponseEntity<String> saveUser() {
        userService.save("b@gmail.com","123456789");
        return ResponseEntity.ok(
                "Create account successful! Please check your email to activated your account!");

    }
}
