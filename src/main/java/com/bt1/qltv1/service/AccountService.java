package com.bt1.qltv1.service;

import com.bt1.qltv1.dto.mfa.VerifyMfaRequest;
import com.bt1.qltv1.entity.Account;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {
    Account findFirstByEmail(String email);
    void increaseFailedAttempts(String email, int failedAttempts);

    void lockAccount(Account account);

    void unlockAccount(Account account);

    String getTimeRemaining(Account account);

    void resetFailedAttempts(String email);

    void enableMfa(VerifyMfaRequest request);

    void disableMfa();
}
