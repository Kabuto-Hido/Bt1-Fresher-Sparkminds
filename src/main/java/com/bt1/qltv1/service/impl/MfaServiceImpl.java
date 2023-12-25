package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.dto.auth.MfaResponse;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.service.AuthService;
import com.bt1.qltv1.service.MfaService;
import com.bt1.qltv1.service.UserService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MfaServiceImpl implements MfaService {
    private final UserService userService;
    private GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

    @Override
    public MfaResponse generateSecretKeyAndQrcode(String email) {
        //GoogleAuthenticator gAuth = new GoogleAuthenticator();
        final GoogleAuthenticatorKey secretKey = googleAuthenticator.createCredentials();

        String urlQrCode = GoogleAuthenticatorQRGenerator
                .getOtpAuthURL("SparkMinds's librarian",email,secretKey);

        return MfaResponse.builder()
                .qrCodeUrl(urlQrCode)
                .secretKey(secretKey.getKey()).build();
    }

    @Override
    public boolean verifyOtp(int code) {
        String email = AuthService.GetEmailLoggedIn();
        User user = userService.findFirstByEmail(email);

        return googleAuthenticator.authorize(user.getSecret(),code);
    }
}
