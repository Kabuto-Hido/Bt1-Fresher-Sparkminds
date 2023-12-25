package com.bt1.qltv1.service;

import com.bt1.qltv1.dto.auth.MfaResponse;
import org.springframework.stereotype.Service;

@Service
public interface MfaService {
    MfaResponse generateSecretKeyAndQrcode(String email);
    boolean verifyOtp(String email, String code);
}
