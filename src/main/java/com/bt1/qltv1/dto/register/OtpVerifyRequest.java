package com.bt1.qltv1.dto.register;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OtpVerifyRequest {
    @Email(message = "{user.email.invalid}")
    private String email;
    @Pattern(regexp = "^[0-9]{6}$",message = "{user.otp.invalid}")
    private String otp;
}
