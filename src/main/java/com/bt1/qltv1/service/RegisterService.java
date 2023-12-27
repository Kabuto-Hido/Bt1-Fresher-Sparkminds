package com.bt1.qltv1.service;

import com.bt1.qltv1.dto.register.RegisterRequest;
import com.bt1.qltv1.entity.Role;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.enumeration.ActivateMailType;
import com.bt1.qltv1.enumeration.UserStatus;
import com.bt1.qltv1.exception.BadRequest;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.repository.RoleRepository;
import com.bt1.qltv1.repository.UserRepository;
import com.bt1.qltv1.util.Global;
import com.bt1.qltv1.util.JwtUtil;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

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

    public void register(RegisterRequest registerRequest){
        Optional<User> userByEmail = userRepository.findByEmail(registerRequest.getEmail());
        if (userByEmail.isPresent()) {
            throw new BadRequest("Email duplicate, Please retype!","user.email.email-existed");
        }

        userByEmail.ifPresent(user -> {
            if (!user.getPhone().equals(registerRequest.getPhone())){
                throw new BadRequest("Phone duplicate, Please retype!","user.phone.phone-existed");
            }
        });

//        if (!isValidPhoneNumber("+84"+registerRequest.getPhone())){
//            throw new BadRequest("Phone number invalid","user.phone.invalid");
//        }

        Optional<Role> roleUserOptional = roleRepository.findById(2L);
        log.info(roleUserOptional);
        List<Role> roleUserList = new ArrayList<>();
        roleUserOptional.ifPresent(roleUserList::add);
        Set<Role> roleUserSet = new HashSet<>(roleUserList);

        User newUser = User.builder().fullName(registerRequest.getFullname())
                .status(UserStatus.ACTIVE)
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .roleSet(roleUserSet).build();

//        userRepository.save(newUser);
//
//        sendEmailToActivatedAccount(registerRequest.getEmail(),
//                registerRequest.getMailType());
    }

    public void sendEmailToActivatedAccount(String addressGmail, ActivateMailType type){
        final String token = jwtUtil.generateEmailToken(addressGmail);
        String link = null;
        String otp = null;

        Context context = new Context();
        context.setVariable("email",addressGmail);
        if(type.equals(ActivateMailType.OTP)){
            otp = Global.getOTP();
        }
        if (type.equals(ActivateMailType.LINK)) {
            link = "http://localhost:8082/api/v1/common/confirm-email?token=" + token;

        }
        context.setVariable("otp",otp);
        context.setVariable("url", link);
        String body = templateEngine.process("email-verify-template.html", context);

        emailService.sendAsHtml(addressGmail,
                body, "Verify your gmail for register");
    }
    public void confirmToken(String token){
        if (Boolean.TRUE.equals(jwtUtil.isTokenExpired(token))) {
            throw new BadRequest("Email expired","user.verify-link.expired");
        }
        String email = jwtUtil.extractUsername(token);
        Optional<User> userConfirm = userRepository.findFirstByEmailAndVerifyMail(email,
                true);
        if (userConfirm.isPresent()) {
            throw new BadRequest("Email already verify mail","user.already.verify-mail");
        }
        userRepository.activateAccount(email);
    }

    public void checkEmailBeforeSendMail(User user){
        if(user == null){
            throw new NotFoundException("Your email is not registered.", "user.email.not-exist");
        }

        if (user.isVerifyMail()){
            throw new BadRequest("Your account has been verify mail.","user.already.verify-mail");
        }
    }

}
