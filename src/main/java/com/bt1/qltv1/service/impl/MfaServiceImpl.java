package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.dto.mfa.MfaResponse;
import com.bt1.qltv1.exception.MfaException;
import com.bt1.qltv1.service.MfaService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

@Component
@Log4j
@RequiredArgsConstructor
public class MfaServiceImpl implements MfaService {
    private final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

    @Override
    public MfaResponse generateSecretKeyAndQrcode(String email) {
        final GoogleAuthenticatorKey secretKey = googleAuthenticator.createCredentials();

        String urlQrCode = GoogleAuthenticatorQRGenerator
                .getOtpAuthURL("SparkMinds's librarian",email,secretKey);

        return MfaResponse.builder()
                .qrCodeUrl(urlQrCode)
                .secretKey(secretKey.getKey()).build();
    }

    @Override
    public boolean verifyOtp(String secretKey, String code) {
        if(code.isEmpty()){
            throw new MfaException("Missing MFA token","mfa.code.invalid");
        }

        if(secretKey.isEmpty()){
            throw new MfaException("Missing secret key","mfa.secret-key.invalid");
        }

        int mfaCode = parseCode(code);
        log.info(googleAuthenticator.authorize(secretKey,mfaCode));
        return googleAuthenticator.authorize(secretKey,mfaCode);
    }

    private int parseCode(String codeString) {
        try {
            return Integer.parseInt(codeString);
        } catch (NumberFormatException e) {
            throw new MfaException("Invalid MFA code","mfa.code.invalid");
        }
    }
}
