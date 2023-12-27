package com.bt1.qltv1.controller.common;

import com.bt1.qltv1.dto.SuccessResponseDTO;
import com.bt1.qltv1.dto.register.RegisterRequest;
import com.bt1.qltv1.dto.register.SendMailRequest;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.service.RegisterService;
import com.bt1.qltv1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/common")
public class RegisterController {
    private final UserService userService;
    private final RegisterService registerService;
    @PostMapping("/register")
    public ResponseEntity<SuccessResponseDTO> registerNewUser(@Valid @RequestBody RegisterRequest registerRequest) {
        registerService.register(registerRequest);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Create account successful!" +
                        " Please check your email to activated your account!"));

    }

    @PostMapping("/send-email/activate")
    public ResponseEntity<SuccessResponseDTO> sendActivatedToEmail(@Valid @RequestBody
                                                                       SendMailRequest sendMailRequest) {
        User user = userService.findFirstByEmail(sendMailRequest.getEmail());
        //check email is exist and is enabled verify email
        registerService.checkEmailBeforeSendMail(user);

        registerService.sendEmailToActivatedAccount(sendMailRequest.getEmail(),
                sendMailRequest.getMailType());
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Send email successful!" +
                        " Please check your email to activated your account!"));
    }

    @GetMapping("/confirm-email")
    public ResponseEntity<SuccessResponseDTO> activatedAccount(@RequestParam("token") String token) {
        registerService.confirmToken(token);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Account has been activated"));
    }
}
