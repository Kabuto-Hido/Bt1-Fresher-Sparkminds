package com.bt1.qltv1.controller.common;

import com.bt1.qltv1.dto.register.RegisterRequest;
import com.bt1.qltv1.dto.register.SendMailRequest;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.service.RegisterService;
import com.bt1.qltv1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/v1/common")
public class RegisterController {
    private final UserService userService;
    private final RegisterService registerService;
    @PostMapping("/register")
    public ResponseEntity<String> registerNewUser(@Valid @RequestBody RegisterRequest registerRequest) {
        registerService.register(registerRequest);
        return ResponseEntity.ok(
                "Create account successful! Please check your email to activated your account!");

    }

    @PostMapping("/sendEmail/activate")
    public ResponseEntity<Object> sendActivatedToEmail(@RequestBody @Valid SendMailRequest sendMailRequest) {
        User user = userService.findFirstByEmail(sendMailRequest.getEmail());
        if(user == null){
            return ResponseEntity.badRequest().body("Your email is not registered or has already been used.");
        }

        if (user.isVerifyMail()){
            return ResponseEntity.badRequest().body("Your account has been verify mail.");
        }

        userService.sendEmailToActivatedAccount(sendMailRequest.getEmail(),sendMailRequest.getMailType());
        return ResponseEntity.ok(
                "Send email successful! Please check your email to activated your account!");
    }

    @GetMapping("/confirm-email")
    public String activatedAccount(@RequestParam("token") String token) {
        return userService.confirmToken(token);
    }
}
