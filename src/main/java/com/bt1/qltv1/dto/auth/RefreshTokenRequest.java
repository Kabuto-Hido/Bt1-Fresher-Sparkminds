package com.bt1.qltv1.dto.auth;

import lombok.*;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class RefreshTokenRequest {
    @NotNull
    private String refreshToken;
}
