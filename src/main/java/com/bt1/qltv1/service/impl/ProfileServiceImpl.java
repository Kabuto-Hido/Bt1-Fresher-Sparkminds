package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.dto.password.ChangePasswordRequest;
import com.bt1.qltv1.dto.register.OtpVerifyRequest;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.exception.BadRequest;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.repository.SessionRepository;
import com.bt1.qltv1.repository.UserRepository;
import com.bt1.qltv1.service.EmailService;
import com.bt1.qltv1.service.ProfileService;
import com.bt1.qltv1.util.Global;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import java.time.LocalDateTime;

@Component
@Log4j
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final EmailService emailService;
    private final SpringTemplateEngine templateEngine;
    private final PasswordEncoder passwordEncoder;

    private User findUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(()->
                new NotFoundException("Your email not exist!","user.email.not-exist"));
    }

    @Transactional(rollbackFor = MessagingException.class)
    @Override
    public void resetPassword(String email) {
        User user = findUserByEmail(email);

        String newPassword = passwordEncoder.encode(Global.NEW_RESET_PASSWORD);
        user.setPassword(newPassword);
        userRepository.save(user);

        //block all session before
        sessionRepository.blockAllSessionByUserId(user.getId());

        Context context = new Context();
        context.setVariable("newPassword", Global.NEW_RESET_PASSWORD);

        sendEmail(context,"reset-password-template.html",
                email,"Reset password Sparkminds");
    }

    @Transactional(rollbackFor = MessagingException.class)
    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        String email = UserDetailsServiceImpl.GetEmailLoggedIn();

        User user = userRepository.findByEmail(email).orElseThrow(()->
                new NotFoundException("Your email not exist!","user.email.not-exist"));

        //check new password different old password
        if(checkPasswordIsValid(changePasswordRequest.getNewPassword(), user.getPassword())){
            throw new BadRequest("New password like old password","user.new-password.invalid");
        }

        //check old password is validate with user
        if(!checkPasswordIsValid(changePasswordRequest.getOldPassword(), user.getPassword())){
            throw new BadRequest("Old password wrong!","user.old-password.invalid");
        }

        //block all session before
        sessionRepository.blockAllSessionByUserId(user.getId());

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);

    }

    @Transactional(rollbackFor = MessagingException.class)
    @Override
    public void changeEmail(String newEmail) {
        String oldEmail = UserDetailsServiceImpl.GetEmailLoggedIn();
        if(oldEmail.equals(newEmail)){
            throw new BadRequest("Email duplicate, Please retype!", "user.email.email-existed");

        }

        User user = findUserByEmail(oldEmail);

        LocalDateTime verifyExpired = LocalDateTime.now().plusMinutes(15);
        String otp = Global.getOTP();
        user.setOtp(otp);
        user.setOtpExpired(verifyExpired);

        userRepository.save(user);

        Context context = new Context();
        context.setVariable("email", newEmail);
        context.setVariable("otp", otp);

        sendEmail(context,"email-verify-template.html",
                newEmail,"Verify new mail for changing!");
    }

    @Override
    public void verifyNewEmailOtp(OtpVerifyRequest otpVerifyRequest) {
        String oldEmail = UserDetailsServiceImpl.GetEmailLoggedIn();

        User user = findUserByEmail(oldEmail);

        LocalDateTime now = LocalDateTime.now();
        if(user.getOtpExpired().isBefore(now)){
            throw new BadRequest("OTP expired", "user.verify-link.expired");
        }

        if (!user.getOtp().equals(otpVerifyRequest.getOtp())){
            throw new BadRequest("Otp is invalid", "user.otp.invalid");
        }

        user.setEmail(otpVerifyRequest.getEmail());
        user.setOtp(null);
        user.setOtpExpired(null);

        //block all session before
        sessionRepository.blockAllSessionByUserId(user.getId());

        userRepository.save(user);
    }

    private boolean checkPasswordIsValid(String rawPassword, String encodePassword){
        return passwordEncoder.matches(rawPassword, encodePassword);
    }

    private void sendEmail(Context context, String template, String addressGmail, String subject) {
        String body = templateEngine.process(template, context);
        emailService.sendAsHtml(addressGmail,
                body, subject);
    }
}
