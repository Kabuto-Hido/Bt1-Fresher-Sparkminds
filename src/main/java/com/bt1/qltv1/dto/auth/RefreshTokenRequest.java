package com.bt1.qltv1.dto.auth;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class RefreshTokenRequest {
    private String refreshToken;
}
