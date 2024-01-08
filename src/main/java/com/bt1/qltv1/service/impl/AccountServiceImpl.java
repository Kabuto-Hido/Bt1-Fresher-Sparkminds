package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.dto.mfa.VerifyMfaRequest;
import com.bt1.qltv1.entity.Account;
import com.bt1.qltv1.enumeration.UserStatus;
import com.bt1.qltv1.exception.BadRequest;
import com.bt1.qltv1.exception.MfaException;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.repository.AccountRepository;
import com.bt1.qltv1.service.AccountService;
import com.bt1.qltv1.service.MfaService;
import com.bt1.qltv1.util.Global;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@Log4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final MfaService mfaService;

    @Override
    public Account findFirstByEmail(String email) {
        return accountRepository.findByEmail(email).orElseThrow(() ->
                new NotFoundException("Wrong email!!", "account.email.not-exist"));
    }

    @Override
    public void increaseFailedAttempts(String email, int failedAttempts) {
        accountRepository.updateFailedAttempts(email, (failedAttempts + 1));
    }

    @Override
    public void lockAccount(Account account) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lockTime = now.plusMinutes((Global.LOCK_TIME_DURATION / 60000));
        account.setLockTime(lockTime);
        account.setStatus(UserStatus.BLOCK);
        log.info("lock time "+ lockTime);
        accountRepository.save(account);
    }

    @Override
    public void unlockAccount(Account account) {
        account.setStatus(UserStatus.ACTIVE);
        account.setLockTime(null);
        account.setFailedAttempt(0);

        accountRepository.save(account);
    }

    @Override
    public String getTimeRemaining(Account account) {
        LocalDateTime d1 = account.getLockTime();

        if(d1 == null){
            return null;
        }

        LocalDateTime d2 = LocalDateTime.now();

        Duration duration = Duration.between(d2, d1);
        long minuteRemaining = duration.getSeconds() / 60;
        long secondRemaining = duration.getSeconds() % 60;
        return String.format("%sm %ss", minuteRemaining, secondRemaining);
    }

    @Override
    public void resetFailedAttempts(String email) {
        accountRepository.updateFailedAttempts(email, 0);
    }

    @Transactional
    @Override
    public void enableMfa(VerifyMfaRequest request) {
        try {
            String email = UserDetailsServiceImpl.getEmailLoggedIn();

            if(!mfaService.verifyOtp(request.getSecret(),request.getCode())){
                throw new MfaException("Invalid MFA code","mfa.code.invalid");
            }

            Account account = findFirstByEmail(email);

            account.setMfaEnabled(true);
            account.setSecret(request.getSecret());

            accountRepository.save(account);
        }catch (Exception ex){
            log.error(ex.getMessage());
            throw new BadRequest(ex.getMessage(),"enable-mfa.error");
        }
        finally {
            log.info("Enable MFA success");
        }
    }
    @Transactional
    @Override
    public void disableMfa() {
        try {
            String email = UserDetailsServiceImpl.getEmailLoggedIn();
            Account account = findFirstByEmail(email);

            account.setMfaEnabled(false);
            account.setSecret(null);

            accountRepository.save(account);
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        finally {
            log.info("Disable MFA success");
        }
    }
}
