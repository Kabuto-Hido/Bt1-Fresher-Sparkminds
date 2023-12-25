package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.dto.auth.MfaResponse;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.exception.MfaException;
import com.bt1.qltv1.service.AuthService;
import com.bt1.qltv1.service.MfaService;
import com.bt1.qltv1.service.UserService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log4j
@RequiredArgsConstructor
public class MfaServiceImpl implements MfaService {
    private final UserService userService;
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
    public boolean verifyOtp(String email, String code) {
        User user = userService.findFirstByEmail(email);

        if(code.isEmpty()){
            throw new MfaException("Missing MFA token");
        }

        int mfaCode = parseCode(code);
        log.info(googleAuthenticator.authorize(user.getSecret(),mfaCode));
        return googleAuthenticator.authorize(user.getSecret(),mfaCode);
    }

    private int parseCode(String codeString) {
        try {
            return Integer.parseInt(codeString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid MFA code");
        }
    }
}
