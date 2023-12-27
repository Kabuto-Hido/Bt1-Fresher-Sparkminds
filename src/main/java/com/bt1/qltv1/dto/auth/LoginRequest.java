package com.bt1.qltv1.dto.auth;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

public class LoginRequest {
    @Email(message = "{user.email.invalid}")
    private String email;
    @NotNull(message = "Please enter password")
    private String password;
    private String code;
}
