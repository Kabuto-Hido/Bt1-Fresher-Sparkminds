package com.bt1.qltv1.service;

import com.bt1.qltv1.dto.mfa.MfaResponse;
import org.springframework.stereotype.Service;

@Service
public interface MfaService {
    MfaResponse generateSecretKeyAndQrcode(String email);
    boolean verifyOtp(String secretKey, String code);
}
