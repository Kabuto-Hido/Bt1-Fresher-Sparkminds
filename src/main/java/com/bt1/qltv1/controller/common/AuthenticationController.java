package com.bt1.qltv1.controller.common;

import com.bt1.qltv1.dto.auth.*;
import com.bt1.qltv1.dto.mfa.MfaResponse;
import com.bt1.qltv1.dto.mfa.VerifyMfaRequest;
import com.bt1.qltv1.service.AuthService;
import com.bt1.qltv1.service.impl.UserDetailsServiceImpl;
import com.bt1.qltv1.service.MfaService;
import com.bt1.qltv1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/v1/common")
public class AuthenticationController {
    private final AuthService authService;
    private final UserService userService;
    private final MfaService mfaService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request){
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @GetMapping("/generate-mfa")
    public ResponseEntity<MfaResponse> generateMfa(){
        String email = UserDetailsServiceImpl.GetEmailLoggedIn();
        return ResponseEntity.ok(mfaService.generateSecretKeyAndQrcode(email));
    }

    @PostMapping("/disable-mfa")
    public ResponseEntity<String> disableMfa(){
        userService.disableMfa();
        return ResponseEntity.ok("Update MFA success!");
    }

    @PostMapping("/enable-mfa")
    public ResponseEntity<String> verifyMfaCode(@RequestBody VerifyMfaRequest verifyMfaRequest){
        userService.enableMfa(verifyMfaRequest);
        return ResponseEntity.ok("Enable MFA success!");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request){
        authService.logout(request);
        return ResponseEntity.ok("Logout success!!!");
    }
}
