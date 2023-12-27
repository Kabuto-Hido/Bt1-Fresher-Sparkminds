package com.bt1.qltv1.dto.mfa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VerifyMfaRequest {
    @NotNull(message = "{mfa.secret-key.invalid}")
    private String secret;
    @Pattern(regexp = "^[0-9]{6}$",message = "{mfa.code.invalid}")
    private String code;
}
