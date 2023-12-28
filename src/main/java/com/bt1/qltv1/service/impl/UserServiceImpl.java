package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.dto.mfa.VerifyMfaRequest;
import com.bt1.qltv1.dto.user.ProfileResponse;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.enumeration.UserStatus;
import com.bt1.qltv1.exception.BadRequest;
import com.bt1.qltv1.exception.MfaException;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.mapper.UserMapper;
import com.bt1.qltv1.repository.UserRepository;
import com.bt1.qltv1.service.MfaService;
import com.bt1.qltv1.service.UserService;
import com.bt1.qltv1.util.ApplicationUser;
import com.bt1.qltv1.util.Global;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Log4j
@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MfaService mfaService;

    @Override
    public List<ProfileResponse> findAllUser() {
        List<User> userList = userRepository.findAll(
                Sort.by(Sort.Direction.DESC, "createdDate"));

        List<ProfileResponse> profileResponses = new ArrayList<>();
        for (User user : userList){
            profileResponses.add(UserMapper.toProfileDto(user));
        }
        return profileResponses;
    }

    @Override
    public User findFirstByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new NotFoundException("Wrong email!!","user.email.not-exist"));
    }

    @Override
    public void increaseFailedAttempts(String email, int failedAttempt) {
        log.info("login fail attempts "+ (failedAttempt+1));
//        User user = findFirstByEmail(email);
//        user.setFailedAttempt((failedAttempt + 1));
//        userRepository.save(user);
        userRepository.updateFailedAttempts(email, (failedAttempt + 1));
    }

    @Override
    public void lockAccount(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lockTime = now.plusMinutes((Global.LOCK_TIME_DURATION / 60000));
        user.setLockTime(lockTime);
        user.setStatus(UserStatus.BLOCK);
        log.info("lock time "+ lockTime);
        userRepository.save(user);

        log.info("lock account:" +user.getEmail() + " in time "+user.getLockTime());
    }

    //@Transactional(readOnly = true)
    @Override
    public void unlockAccount(User user) {
        user.setStatus(UserStatus.ACTIVE);
        user.setLockTime(null);
        user.setFailedAttempt(0);

        userRepository.save(user);
        log.info("User after unlock: "+ user);
    }

    @Override
    public String getTimeRemaining(User user) {
        LocalDateTime d1 = user.getLockTime();
        LocalDateTime d2 = LocalDateTime.now();

        Duration duration = Duration.between(d2, d1);
        long minuteRemaining = duration.getSeconds() / 60;
        long secondRemaining = duration.getSeconds() % 60;
        return String.format("%sm %ss", minuteRemaining, secondRemaining);
    }

    @Override
    public void resetFailedAttempts(String email) {
        userRepository.updateFailedAttempts(email, 0);
    }

    @Override
    public void enableMfa(VerifyMfaRequest request) {
        try {
            String email = UserDetailsServiceImpl.GetEmailLoggedIn();

            if(!mfaService.verifyOtp(request.getSecret(),request.getCode())){
                throw new MfaException("Invalid MFA code","mfa.code.invalid");
            }

            User user = findFirstByEmail(email);

            user.setMfaEnabled(true);
            user.setSecret(request.getSecret());

            userRepository.save(user);
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        finally {
            log.info("Enable MFA success");
        }
    }

    @Transactional
    @Override
    public void disableMfa() {
        try {
            String email = UserDetailsServiceImpl.GetEmailLoggedIn();
            User user = findFirstByEmail(email);

            user.setMfaEnabled(false);
            user.setSecret(null);

            userRepository.save(user);
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        finally {
            log.info("Disable MFA success");
        }

    }

    @Override
    public void updateAvatar() {
        try {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((ApplicationUser)principal).getUsername();

        User user = findFirstByEmail(email);
        user.setAvatar(Global.DEFAULT_AVATAR);
        userRepository.save(user);

    } catch (Exception e) {
        log.error("An error occurred in updateAvatar method", e);
        throw new BadRequest("An error occurred in updateAvatar method",
                "user.avatar.fail");
    }
        log.info("updateAvatar method finished ");
    }
}
