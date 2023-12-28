package com.bt1.qltv1.service;

import com.bt1.qltv1.dto.register.OtpVerifyRequest;
import com.bt1.qltv1.dto.register.RegisterRequest;
import com.bt1.qltv1.dto.register.SendMailRequest;
import com.bt1.qltv1.entity.Role;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.enumeration.UserStatus;
import com.bt1.qltv1.exception.BadRequest;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.repository.RoleRepository;
import com.bt1.qltv1.repository.UserRepository;
import com.bt1.qltv1.util.Global;
import com.bt1.qltv1.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Log4j
@RequiredArgsConstructor
public class RegisterService {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final SpringTemplateEngine templateEngine;

    public void register(RegisterRequest registerRequest) {
        Optional<User> userByEmail = userRepository.findByEmail(registerRequest.getEmail());
        if (userByEmail.isPresent()) {
            throw new BadRequest("Email duplicate, Please retype!", "user.email.email-existed");
        }

        userByEmail.ifPresent(user -> {
            if (!user.getPhone().equals(registerRequest.getPhone())) {
                throw new BadRequest("Phone duplicate, Please retype!", "user.phone.phone-existed");
            }
        });

        Optional<Role> roleUserOptional = roleRepository.findById(2L);
        log.info(roleUserOptional);
        List<Role> roleUserList = new ArrayList<>();
        roleUserOptional.ifPresent(roleUserList::add);
        Set<Role> roleUserSet = new HashSet<>(roleUserList);

        //generate token
        String token = jwtUtil.generateEmailToken(registerRequest.getEmail());
        LocalDateTime verifyExpired = jwtUtil.extractExpiration(token);

        String otp = Global.getOTP();
        sendEmailToActivatedAccount(registerRequest.getEmail(), token, otp);

        User newUser = User.builder().fullName(registerRequest.getFullname())
                .status(UserStatus.ACTIVE)
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .otp(otp)
                .otpExpired(verifyExpired)
                .roleSet(roleUserSet).build();

        userRepository.save(newUser);
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
        Optional<User> userConfirm = userRepository.findFirstByEmailAndVerifyMail(email,
                true);
        if (userConfirm.isPresent()) {
            throw new BadRequest("Email already verify mail",
                    "user.confirm.verify-mail");
        }
        userRepository.activateAccount(email);
    }

    public void verifyOtp(OtpVerifyRequest otpVerifyRequest){
        User user = userRepository.findByEmail(otpVerifyRequest.getEmail()).orElseThrow(() ->
                new NotFoundException("Your email is not registered.", "user.email.not-exist"));

        if(user.isVerifyMail()){
            throw new BadRequest("Email already verify mail", "user.already.verify-mail");
        }

        LocalDateTime now = LocalDateTime.now();
        if(user.getOtpExpired().isBefore(now)){
            throw new BadRequest("OTP expired", "user.verify-link.expired");
        }

        if (!user.getOtp().equals(otpVerifyRequest.getOtp())){
            throw new BadRequest("Otp is invalid", "user.otp.invalid");
        }

        userRepository.activateAccount(otpVerifyRequest.getEmail());
    }

    public void resendVerifyMail(SendMailRequest sendMailRequest) {
        User user = userRepository.findByEmail(sendMailRequest.getEmail()).orElseThrow(() ->
                new NotFoundException("Your email is not registered.", "user.email.not-exist"));
        //check email is exist and is enabled verify email
        if (user.isVerifyMail()) {
            throw new BadRequest("Your account has been verify mail.",
                    "user.resend-mail.verify-mail");
        }

        //generate token
        String token = jwtUtil.generateEmailToken(sendMailRequest.getEmail());
        LocalDateTime verifyExpired = jwtUtil.extractExpiration(token);
        String otp = Global.getOTP();

        sendEmailToActivatedAccount(sendMailRequest.getEmail(), token, otp);

        user.setOtp(otp);
        user.setOtpExpired(verifyExpired);
        userRepository.save(user);

    }

}
