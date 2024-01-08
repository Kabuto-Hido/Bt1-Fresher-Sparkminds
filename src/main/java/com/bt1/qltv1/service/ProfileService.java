package com.bt1.qltv1.service;

import com.bt1.qltv1.dto.password.ChangePasswordRequest;
import com.bt1.qltv1.dto.register.OtpVerifyRequest;
import com.bt1.qltv1.dto.user.VerifySms;
import org.springframework.stereotype.Service;

@Service
public interface ProfileService {
    void resetPassword(String email);

    void changePassword(ChangePasswordRequest changePasswordRequest);

    void changeEmail(String newEmail);

    void verifyNewEmailOtp(OtpVerifyRequest otpVerifyRequest);

    void changePhone(String newPhone);

    void verifyNewPhoneSms(VerifySms verifySms);
}
