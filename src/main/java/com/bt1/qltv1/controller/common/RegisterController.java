package com.bt1.qltv1.controller.common;

import com.bt1.qltv1.dto.SuccessResponseDTO;
import com.bt1.qltv1.dto.register.OtpVerifyRequest;
import com.bt1.qltv1.dto.register.RegisterRequest;
import com.bt1.qltv1.dto.register.SendMailRequest;
import com.bt1.qltv1.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/common")
public class RegisterController {
    private final RegisterService registerService;

    @PostMapping("/register")
    public ResponseEntity<SuccessResponseDTO> registerNewUser(@Valid @RequestBody RegisterRequest registerRequest) {
        registerService.register(registerRequest);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.CREATED,
                "Create account successful!" +
                        " Please check your email to activated your account!"));
    }

    @PostMapping("/send-email/activate")
    public ResponseEntity<SuccessResponseDTO> sendActivatedToEmail(@Valid @RequestBody
                                                                       SendMailRequest sendMailRequest) {
        registerService.resendVerifyMail(sendMailRequest);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Send email successful!" +
                        " Please check your email to activated your account!"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<SuccessResponseDTO> activeAccountWithOtp(@Valid @RequestBody
                                                                       OtpVerifyRequest otpVerifyRequest) {
        registerService.verifyOtp(otpVerifyRequest);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Your email has been verified! Please login!"));
    }

    @GetMapping("/confirm-email")
    public ResponseEntity<SuccessResponseDTO> activeAccountWithLink(@RequestParam("token") String token) {
        registerService.confirmToken(token);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Account has been activated"));
    }

}
