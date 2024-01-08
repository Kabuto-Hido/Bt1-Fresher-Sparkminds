package com.bt1.qltv1.service;

import com.bt1.qltv1.dto.register.OtpVerifyRequest;
import com.bt1.qltv1.dto.register.RegisterRequest;
import com.bt1.qltv1.dto.register.SendMailRequest;
import com.bt1.qltv1.entity.Account;
import com.bt1.qltv1.entity.Role;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.enumeration.UserStatus;
import com.bt1.qltv1.exception.BadRequest;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.repository.AccountRepository;
import com.bt1.qltv1.repository.RoleRepository;
import com.bt1.qltv1.repository.UserRepository;
import com.bt1.qltv1.util.Global;
import com.bt1.qltv1.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Log4j
@RequiredArgsConstructor
public class RegisterService {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final SpringTemplateEngine templateEngine;

    @Transactional(rollbackFor = MessagingException.class)
    public void register(RegisterRequest registerRequest) {
        Optional<User> userByEmail = userRepository.findByEmail(registerRequest.getEmail());
        if (userByEmail.isPresent()) {
            throw new BadRequest("Email duplicate, Please retype!",
                    "user.email.email-existed");
        }

        Optional<User> userByPhone = userRepository.findByPhone(registerRequest.getPhone());
        if (userByPhone.isPresent()) {
            throw new BadRequest("Phone duplicate, Please retype!",
                    "user.phone.phone-existed");
        }

        //get user role
        Optional<Role> roleUserOptional = roleRepository.findById(2L);
        log.debug(roleUserOptional);
        List<Role> roleUserList = new ArrayList<>();
        roleUserOptional.ifPresent(roleUserList::add);
        Set<Role> roleUserSet = new HashSet<>(roleUserList);

        //generate token
        String token = jwtUtil.generateEmailToken(registerRequest.getEmail());
        LocalDateTime verifyExpired = jwtUtil.extractExpiration(token);

        String otp = Global.getOTP();

        User newUser = User.builder().fullName(registerRequest.getFullname())
                .status(UserStatus.ACTIVE)
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .phone(registerRequest.getPhone())
                .otp(otp)
                .otpExpired(verifyExpired)
                .roleSet(roleUserSet).build();

        userRepository.save(newUser);

        sendEmailToActivatedAccount(registerRequest.getEmail(), token, otp);

    }

    public void sendEmailToActivatedAccount(String addressGmail, String token, String otp) {
        String link = "http://localhost:8082/api/v1/common/confirm-email?token=" + token;

        Context context = new Context();
        context.setVariable("email", addressGmail);
        context.setVariable("otp", otp);
        context.setVariable("url", link);

        String body = templateEngine.process("email-verify-template.html", context);

        emailService.sendAsHtml(addressGmail,
                body, "Verify your gmail for register");
    }

    public void confirmToken(String token) {
        if (Boolean.TRUE.equals(jwtUtil.isTokenExpired(token))) {
            throw new BadRequest("Link expired", "user.verify-link.expired");
        }
        String email = jwtUtil.extractUsername(token);
        Optional<Account> accountConfirm = accountRepository.findFirstByEmailAndVerifyMail(email,
                true);
        if (accountConfirm.isPresent()) {
            throw new BadRequest("Email already verify mail",
                    "user.confirm.verify-mail");
        }
        accountRepository.activateAccount(email);
    }

    public void verifyOtp(OtpVerifyRequest otpVerifyRequest){
        Account account = accountRepository.findByEmail(otpVerifyRequest.getEmail()).orElseThrow(() ->
                new NotFoundException("Your email is not registered.", "user.email.not-exist"));

        if(account.isVerifyMail()){
            throw new BadRequest("Email already verify mail", "user.already.verify-mail");
        }

        LocalDateTime now = LocalDateTime.now();
        if(account.getOtpExpired().isBefore(now)){
            throw new BadRequest("OTP expired", "user.verify-link.expired");
        }

        if (!account.getOtp().equals(otpVerifyRequest.getOtp())){
            throw new BadRequest("Otp is invalid", "user.otp.invalid");
        }

        accountRepository.activateAccount(otpVerifyRequest.getEmail());
    }

    public void resendVerifyMail(SendMailRequest sendMailRequest) {
        Account account = accountRepository.findByEmail(sendMailRequest.getEmail()).orElseThrow(() ->
                new NotFoundException("Your email is not registered.", "user.email.not-exist"));

        //check email is exist and is enabled verify email
        if (account.isVerifyMail()) {
            throw new BadRequest("Your account has been verify mail.",
                    "user.resend-mail.verify-mail");
        }

        //generate token
        String token = jwtUtil.generateEmailToken(sendMailRequest.getEmail());
        LocalDateTime verifyExpired = jwtUtil.extractExpiration(token);
        String otp = Global.getOTP();

        sendEmailToActivatedAccount(sendMailRequest.getEmail(), token, otp);

        account.setOtp(otp);
        account.setOtpExpired(verifyExpired);
        accountRepository.save(account);

    }

}
