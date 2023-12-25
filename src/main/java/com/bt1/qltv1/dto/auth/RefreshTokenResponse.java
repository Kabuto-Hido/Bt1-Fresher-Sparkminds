package com.bt1.qltv1.dto.auth;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;
}
