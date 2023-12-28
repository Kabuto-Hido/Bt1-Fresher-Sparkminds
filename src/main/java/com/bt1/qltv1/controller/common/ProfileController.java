package com.bt1.qltv1.controller.common;

import com.bt1.qltv1.dto.SuccessResponseDTO;
import com.bt1.qltv1.dto.password.ChangePasswordRequest;
import com.bt1.qltv1.dto.register.OtpVerifyRequest;
import com.bt1.qltv1.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/common/reset-password")
    public ResponseEntity<SuccessResponseDTO> resetPassword(@RequestParam @Email(message = "{user.email.invalid}")
                                                          String email){
        profileService.resetPassword(email);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Send new password to email successful!"));
    }

    @PostMapping("/user/change-password")
    public ResponseEntity<SuccessResponseDTO> changePassword(@RequestBody @Valid
                                                                 ChangePasswordRequest changePasswordRequest){
        profileService.changePassword(changePasswordRequest);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Change password successful!Please login again!"));
    }

    @PostMapping("/user/change-email")
    public ResponseEntity<SuccessResponseDTO> changeEmail(@RequestParam(name = "email")
                                                              @Email(message = "{user.email.invalid}")
                                                              String newEmail){
        profileService.changeEmail(newEmail);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Send mail success! Please confirm your new email!"));
    }

    @PostMapping("/user/new-email/verify-otp")
    public ResponseEntity<SuccessResponseDTO> verifyNewEmailWithOtp(@Valid @RequestBody
                                                                   OtpVerifyRequest otpVerifyRequest) {
        profileService.verifyNewEmailOtp(otpVerifyRequest);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Your email has been verified! Please login!"));
    }
}
