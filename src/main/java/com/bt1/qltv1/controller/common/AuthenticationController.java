package com.bt1.qltv1.controller.common;

import com.bt1.qltv1.dto.SuccessResponseDTO;
import com.bt1.qltv1.dto.auth.LoginRequest;
import com.bt1.qltv1.dto.auth.LoginResponse;
import com.bt1.qltv1.dto.auth.RefreshTokenRequest;
import com.bt1.qltv1.dto.auth.RefreshTokenResponse;
import com.bt1.qltv1.dto.mfa.MfaResponse;
import com.bt1.qltv1.dto.mfa.VerifyMfaRequest;
import com.bt1.qltv1.service.AccountService;
import com.bt1.qltv1.service.AuthService;
import com.bt1.qltv1.service.MfaService;
import com.bt1.qltv1.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/common")
public class AuthenticationController {
    private final AuthService authService;
    private final AccountService accountService;
    private final MfaService mfaService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody
                                                                 RefreshTokenRequest request){
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @GetMapping("/generate-mfa")
    public ResponseEntity<MfaResponse> generateMfa(){
        String email = UserDetailsServiceImpl.getEmailLoggedIn();
        return ResponseEntity.ok(mfaService.generateSecretKeyAndQrcode(email));
    }

    @PostMapping("/disable-mfa")
    public ResponseEntity<SuccessResponseDTO> disableMfa(){
        accountService.disableMfa();
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Disable MFA success!"));
    }

    @PostMapping("/enable-mfa")
    public ResponseEntity<SuccessResponseDTO> verifyMfaCode(@Valid @RequestBody
                                                                VerifyMfaRequest verifyMfaRequest){
        accountService.enableMfa(verifyMfaRequest);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Enable MFA success!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<SuccessResponseDTO> logout(HttpServletRequest request){
        authService.logout(request);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Logout success!!!"));
    }
}
