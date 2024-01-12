package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.dto.password.ChangePasswordRequest;
import com.bt1.qltv1.dto.register.OtpVerifyRequest;
import com.bt1.qltv1.dto.user.VerifySms;
import com.bt1.qltv1.entity.Account;
import com.bt1.qltv1.entity.Otp;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.enumeration.OtpType;
import com.bt1.qltv1.exception.BadRequest;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.repository.AccountRepository;
import com.bt1.qltv1.repository.OtpRepository;
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
import java.util.Optional;

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
    private final OtpRepository otpRepository;

    private Account getCurrentAccount(){
        String email = UserDetailsServiceImpl.getEmailLoggedIn();
        return findAccByEmail(email);
    }

    private Otp getOtpByAccountIdAndType(long accId, OtpType type){
        return otpRepository.findByAccountIdAndType(accId, type)
                .orElseThrow(()->
                        new NotFoundException(String.format("Not found otp with account %s type %s",
                                accId,type), "otp.invalid"));
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

        //check new email
        Optional<User> checkNewEmail = userRepository.findByEmail(newEmail);
        if (checkNewEmail.isPresent()) {
            throw new BadRequest("Email duplicate, Please retype!",
                    "account.email.email-existed");
        }

        Account account = findAccByEmail(oldEmail);



        LocalDateTime verifyExpired = LocalDateTime.now().plusMinutes(15);
        String code = Global.getOTP();

        Otp otpEntity;
        Optional<Otp> otpOptional = otpRepository.findByAccountIdAndType(account.getId(),
                OtpType.CHANGE_EMAIL);
        if(otpOptional.isPresent()){
            otpEntity = otpOptional.get();
        }
        else {
            otpEntity = new Otp();
            otpEntity.setType(OtpType.CHANGE_EMAIL);
            otpEntity.setAccount(account);
        }

        otpEntity.setCode(code);
        otpEntity.setOtpExpired(verifyExpired);
        otpRepository.save(otpEntity);

        Context context = new Context();
        context.setVariable("email", newEmail);
        context.setVariable("otp", code);

        sendEmail(context,"email-verify-template.html",
                newEmail,"Verify new mail for changing!");
    }

    @Override
    public void verifyNewEmailOtp(OtpVerifyRequest otpVerifyRequest) {
        Account account = getCurrentAccount();
        Otp otp = getOtpByAccountIdAndType(account.getId(), OtpType.CHANGE_EMAIL);

        LocalDateTime now = LocalDateTime.now();
        if(otp.getOtpExpired().isBefore(now)){
            throw new BadRequest("OTP expired", "account.verify-link.expired");
        }

        if (!otp.getCode().equals(otpVerifyRequest.getOtp())){
            throw new BadRequest("Otp is invalid", "account.otp.invalid");
        }

        //block all session before
        sessionRepository.blockAllSessionByAccId(account.getId());

        otp.setCode(null);
        otp.setOtpExpired(null);
        otpRepository.save(otp);

        account.setEmail(otpVerifyRequest.getEmail());
        accountRepository.save(account);
    }

    @Override
    public void changePhone(String newPhone) {
        Optional<User> checkNewPhone = userRepository.findByPhone(newPhone);
        if(checkNewPhone.isPresent()){
            throw new BadRequest("Phone duplicate, Please retype!",
                    "user.phone.phone-existed");
        }

        String email = UserDetailsServiceImpl.getEmailLoggedIn();
        User user = userRepository.findByEmail(email).orElseThrow(()->
                new NotFoundException("Your email not exist!","user.email.not-exist"));

        LocalDateTime otpExpired = LocalDateTime.now().plusMinutes(15);
        String otp = Global.getOTP();

        Otp otpEntity;
        Optional<Otp> otpOptional = otpRepository.findByAccountIdAndType(user.getId(),
                OtpType.CHANGE_PHONE);
        if(otpOptional.isPresent()){
            otpEntity = otpOptional.get();
        }
        else {
            otpEntity = new Otp();
            otpEntity.setType(OtpType.CHANGE_PHONE);
            otpEntity.setAccount(user);
        }

        otpEntity.setCode(otp);
        otpEntity.setOtpExpired(otpExpired);

        otpRepository.save(otpEntity);
    }

    @Override
    public void verifyNewPhoneSms(VerifySms verifySms) {
        String email = UserDetailsServiceImpl.getEmailLoggedIn();
        User user = userRepository.findByEmail(email).orElseThrow(()->
                new NotFoundException("Not found email!","user.email.not-exist"));

        Otp otp = getOtpByAccountIdAndType(user.getId(), OtpType.CHANGE_PHONE);

        LocalDateTime now = LocalDateTime.now();
        if(otp.getOtpExpired().isBefore(now)){
            throw new BadRequest("OTP expired", "user.verify-link.expired");
        }

        if (!otp.getCode().equals(verifySms.getOtp())){
            throw new BadRequest("Otp is invalid", "user.otp.invalid");
        }

        otp.setCode(null);
        otp.setOtpExpired(null);
        otpRepository.save(otp);

        user.setPhone(verifySms.getPhone());
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
