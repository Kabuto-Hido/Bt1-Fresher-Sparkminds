package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.dto.password.ChangePasswordRequest;
import com.bt1.qltv1.dto.register.OtpVerifyRequest;
import com.bt1.qltv1.dto.user.VerifySms;
import com.bt1.qltv1.entity.Account;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.exception.BadRequest;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.repository.AccountRepository;
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
    private final AccountRepository accountRepository;
    private final SessionRepository sessionRepository;
    private final EmailService emailService;
    private final SpringTemplateEngine templateEngine;
    private final PasswordEncoder passwordEncoder;

    private Account getCurrentAccount(){
        String email = UserDetailsServiceImpl.getEmailLoggedIn();
        return findAccByEmail(email);
    }

    private Account findAccByEmail(String email){
        return accountRepository.findByEmail(email).orElseThrow(()->
                new NotFoundException("Your email not exist!","account.email.not-exist"));
    }

    @Transactional(rollbackFor = MessagingException.class)
    @Override
    public void resetPassword(String email) {
        Account account = findAccByEmail(email);

        String newPassword = passwordEncoder.encode(Global.NEW_RESET_PASSWORD);
        account.setPassword(newPassword);
        accountRepository.save(account);

        //block all session before
        sessionRepository.blockAllSessionByAccId(account.getId());

        Context context = new Context();
        context.setVariable("newPassword", Global.NEW_RESET_PASSWORD);

        sendEmail(context,"reset-password-template.html",
                email,"Reset password Sparkminds");
    }

    @Transactional(rollbackFor = MessagingException.class)
    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        Account account = getCurrentAccount();

        //check new password different old password
        if(checkPasswordIsValid(changePasswordRequest.getNewPassword(), account.getPassword())){
            throw new BadRequest("New password like old password","account.new-password.invalid");
        }

        //check old password is validation with user
        if(!checkPasswordIsValid(changePasswordRequest.getOldPassword(), account.getPassword())){
            throw new BadRequest("Old password wrong!","account.old-password.invalid");
        }

        //block all session before
        sessionRepository.blockAllSessionByAccId(account.getId());

        account.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        accountRepository.save(account);

    }

    @Transactional(rollbackFor = MessagingException.class)
    @Override
    public void changeEmail(String newEmail) {
        String oldEmail = UserDetailsServiceImpl.getEmailLoggedIn();
        if (oldEmail != null && oldEmail.equals(newEmail)) {
            throw new BadRequest("Email duplicate, Please retype!",
                    "account.email.email-existed");
        }

        Account account = findAccByEmail(oldEmail);

        LocalDateTime verifyExpired = LocalDateTime.now().plusMinutes(15);
        String otp = Global.getOTP();
        account.setOtp(otp);
        account.setOtpExpired(verifyExpired);

        accountRepository.save(account);

        Context context = new Context();
        context.setVariable("email", newEmail);
        context.setVariable("otp", otp);

        sendEmail(context,"email-verify-template.html",
                newEmail,"Verify new mail for changing!");
    }

    @Override
    public void verifyNewEmailOtp(OtpVerifyRequest otpVerifyRequest) {
        Account account = getCurrentAccount();

        LocalDateTime now = LocalDateTime.now();
        if(account.getOtpExpired().isBefore(now)){
            throw new BadRequest("OTP expired", "account.verify-link.expired");
        }

        if (!account.getOtp().equals(otpVerifyRequest.getOtp())){
            throw new BadRequest("Otp is invalid", "account.otp.invalid");
        }

        account.setEmail(otpVerifyRequest.getEmail());
        account.setOtp(null);
        account.setOtpExpired(null);

        //block all session before
        sessionRepository.blockAllSessionByAccId(account.getId());

        accountRepository.save(account);
    }

    @Override
    public void changePhone(String newPhone) {
        String email = UserDetailsServiceImpl.getEmailLoggedIn();

        User user = userRepository.findByEmail(email).orElseThrow(()->
                new NotFoundException("Your email not exist!","user.email.not-exist"));

        if(newPhone.equals(user.getPhone())){
            throw new BadRequest("Phone duplicate, Please retype!",
                    "user.phone.phone-existed");
        }

        LocalDateTime otpExpired = LocalDateTime.now().plusMinutes(15);
        String otp = Global.getOTP();
        user.setOtp(otp);
        user.setOtpExpired(otpExpired);

        userRepository.save(user);
    }

    @Override
    public void verifyNewPhoneSms(VerifySms verifySms) {
        String email = UserDetailsServiceImpl.getEmailLoggedIn();
        User user = userRepository.findByEmail(email).orElseThrow(()->
                new NotFoundException("Not found email!","user.email.not-exist"));

        LocalDateTime now = LocalDateTime.now();
        if(user.getOtpExpired().isBefore(now)){
            throw new BadRequest("OTP expired", "user.verify-link.expired");
        }

        if (!user.getOtp().equals(verifySms.getOtp())){
            throw new BadRequest("Otp is invalid", "user.otp.invalid");
        }

        user.setPhone(verifySms.getPhone());
        user.setOtp(null);
        user.setOtpExpired(null);

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
