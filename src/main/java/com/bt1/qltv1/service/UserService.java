package com.bt1.qltv1.service;

import com.bt1.qltv1.dto.mfa.VerifyMfaRequest;
import com.bt1.qltv1.dto.user.ProfileResponse;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.enumeration.ActivateMailType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    List<ProfileResponse> findAllUser();
    User findFirstByEmail(String email);
    void increaseFailedAttempts(String email, int failedAttempts);
    void lockAccount(User user);
    void unlockAccount(User user);
    String getTimeRemaining(User user);
    void resetFailedAttempts(String email);
    void enableMfa(VerifyMfaRequest request);
    void disableMfa();
    void updateAvatar();
    void sendEmailToActivatedAccount(String addressGmail, ActivateMailType type);
    String confirmToken(String token);
}
