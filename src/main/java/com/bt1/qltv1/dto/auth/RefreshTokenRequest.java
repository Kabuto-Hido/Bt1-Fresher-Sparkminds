package com.bt1.qltv1.dto.auth;

import lombok.*;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class RefreshTokenRequest {
    @NotNull(message = "{Refresh token may be not null}")
    private String refreshToken;
}
