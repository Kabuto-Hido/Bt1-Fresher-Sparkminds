package com.bt1.qltv1.util;

import com.bt1.qltv1.entity.Account;
import com.bt1.qltv1.exception.AuthException;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.service.AccountService;
import lombok.extern.log4j.Log4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Log4j
@Component
public class AuthenticationEventListener {
    private final AccountService accountService;

    public AuthenticationEventListener(AccountService accountService) {
        this.accountService = accountService;
    }

    @EventListener
    public void authenticationSuccess(AuthenticationSuccessEvent event) {
        ApplicationUser applicationUser  = (ApplicationUser) event.getAuthentication()
                .getPrincipal();

        Account account = accountService.findFirstByEmail(applicationUser.getUsername());

        log.info("login success");

        log.info(account.isVerifyMail());

        if (!account.isVerifyMail()){
            throw new AuthException("You must verify your email first to login!!",
                    "user.not-verify-mail");
        }

        //if fail attempts larger than 0
        if (account.getFailedAttempt() > 0) {
            accountService.resetFailedAttempts(account.getEmail());
        }

        if (!account.isBlock()) return;

        if (!account.isLockTimeExpired()) {
            log.warn("Account "+ account.getEmail()+" will unlock after "+
                    accountService.getTimeRemaining(account));
            throw new AuthException("Your account will unlock after "+
                    accountService.getTimeRemaining(account), "user.status.block");
        }

        log.info("Unlock "+account.getEmail());
        accountService.unlockAccount(account);

    }

    @EventListener
    public void authenticationFailed(AuthenticationFailureBadCredentialsEvent event) {
        String email  = (String) event.getAuthentication().getPrincipal();

        Account account = accountService.findFirstByEmail(email);

        log.info("login fail");
        if(account ==null){
            log.warn("Not found "+email);
            throw new NotFoundException("Email not exist!","user.email.not-exist");
        }

        int loginFailedAttempts = account.getFailedAttempt();

        //check status account is active
        if (account.isActive()){
            if(loginFailedAttempts >= Global.MAX_FAILED_ATTEMPTS){
                accountService.lockAccount(account);
                throw new AuthException("Your account has been locked due to 3 " +
                        "failed attempts. It will be unlocked after 30 minutes.",
                        "user.login.fail");
            }
            accountService.increaseFailedAttempts(account.getEmail(),loginFailedAttempts);
        }

        if (account.isBlock()){  //block
            if (account.isLockTimeExpired()){
                accountService.unlockAccount(account);
                log.info("Unlock "+account.getEmail());
                throw new AuthException("Your account has been unlocked." +
                        " Please try to login again.","user.unblock");
            }

            log.warn("Account "+ account.getEmail()+" will unlock after "+
                    accountService.getTimeRemaining(account));
            throw new AuthException("Your account will unlock after "+
                    accountService.getTimeRemaining(account), "user.status.block");
        }
    }

}
