package com.bt1.qltv1.util;

import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.exception.AuthException;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.service.UserService;
import lombok.extern.log4j.Log4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Log4j
@Component
public class AuthenticationEventListener {
    private final UserService userService;

    public AuthenticationEventListener(UserService userService) {
        this.userService = userService;
    }

    @EventListener
    public void authenticationSuccess(AuthenticationSuccessEvent event) {
        ApplicationUser applicationUser  = (ApplicationUser) event.getAuthentication()
                .getPrincipal();
        User user = userService.findFirstByEmail(applicationUser.getUsername());
        log.info("login success");

        log.info(user.isVerifyMail());

        if (!user.isVerifyMail()){
            throw new AuthException("You must verify your email first to login!!",
                    "user.not-verify-mail");
        }

        //if fail attempts larger than 0
        if (user.getFailedAttempt() > 0) {
            userService.resetFailedAttempts(user.getEmail());
        }

        if (!user.isBlock()) return;

        if (!user.isLockTimeExpired()) {
            log.warn("Account "+ user.getEmail()+" will unlock after "+
                    userService.getTimeRemaining(user));
            throw new AuthException("Your account will unlock after "+
                    userService.getTimeRemaining(user), "user.status.block");
        }

        log.info("Unlock "+user.getEmail());
        userService.unlockAccount(user);

    }

    @EventListener
    public void authenticationFailed(AuthenticationFailureBadCredentialsEvent event) {
        String email  = (String) event.getAuthentication().getPrincipal();
        User user = userService.findFirstByEmail(email);

        log.info("login fail");
        if(user ==null){
            log.warn("Not found "+email);
            throw new NotFoundException("Email not exist!","user.email.not-exist");
        }

        int loginFailedAttempts = user.getFailedAttempt();

        //check status account is active
        if (user.isActive()){
            if(loginFailedAttempts >= Global.MAX_FAILED_ATTEMPTS){
                userService.lockAccount(user);
                throw new AuthException("Your account has been locked due to 3 " +
                        "failed attempts. It will be unlocked after 30 minutes.",
                        "user.login.fail");
            }
            userService.increaseFailedAttempts(user.getEmail(),loginFailedAttempts);
        }

        if (user.isBlock()){  //block
            if (user.isLockTimeExpired()){
                userService.unlockAccount(user);
                log.info("Unlock "+user.getEmail());
                throw new AuthException("Your account has been unlocked." +
                        " Please try to login again.","user.unblock");
            }

            log.warn("Account "+ user.getEmail()+" will unlock after "+
                    userService.getTimeRemaining(user));
            throw new AuthException("Your account will unlock after "+
                    userService.getTimeRemaining(user), "user.status.block");
        }
    }

}
