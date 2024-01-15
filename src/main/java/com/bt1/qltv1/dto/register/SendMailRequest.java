package com.bt1.qltv1.dto.register;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SendMailRequest {
    @Email(message = "{user.email.invalid}")
    private String email;
}
