package com.bt1.qltv1.util;

import com.bt1.qltv1.config.Global;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.exception.LockAccountException;
import com.bt1.qltv1.service.UserService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Log4j
@Component
public class AuthenticationEventListener {
    @Autowired
    private UserService userService;

    @EventListener
    public void authenticationSuccess(AuthenticationSuccessEvent event) {
        ApplicationUser applicationUser  = (ApplicationUser) event.getAuthentication()
                .getPrincipal();
        User user = userService.findFirstByEmail(applicationUser.getUsername());

        if(user.isBlock()) {
            if (user.isLockTimeExpired()) {
                log.info("Unlock "+user.getEmail());
                userService.unlockAccount(user);
            }
            else {
                log.warn("Account "+ user.getEmail()+" will unlock after "+
                        userService.getTimeRemaining(user));
                throw new LockAccountException("Your account will unlock after "+
                        userService.getTimeRemaining(user));
            }
        }
        //if fail attempts larger than 0
        if (user.getFailedAttempt() > 0) {
            userService.resetFailedAttempts(user.getEmail());
        }

    }

    @EventListener
    public void authenticationFailed(AuthenticationFailureBadCredentialsEvent event) {
        String email  = (String) event.getAuthentication().getPrincipal();
        User user = userService.findFirstByEmail(email);

        if(user ==null){
            log.warn("Not found "+email);
            throw new IllegalArgumentException("Email not exist!");
        }

        int loginFailedAttempts = user.getFailedAttempt();

        //check status account is active
        if (user.isActive()){
            if (loginFailedAttempts < Global.MAX_FAILED_ATTEMPTS){
                userService.increaseFailedAttempts(user.getEmail(),loginFailedAttempts);
            }
            else {
                userService.lockAccount(user);
                throw new LockAccountException("Your account has been locked due to 3 " +
                        "failed attempts. It will be unlocked after 30 minutes.");
            }
        }else if (user.isBlock()){  //block
            if (user.isLockTimeExpired()){
                userService.unlockAccount(user);
                log.info("Unlock "+user.getEmail());
                throw new LockAccountException("Your account has been unlocked." +
                        " Please try to login again.");
            }
            else {
                log.warn("Account "+ user.getEmail()+" will unlock after "+
                        userService.getTimeRemaining(user));
                throw new LockAccountException("Your account will unlock after "+
                        userService.getTimeRemaining(user));
            }
        }
    }

}
