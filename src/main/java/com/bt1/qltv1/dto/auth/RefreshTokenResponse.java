package com.bt1.qltv1.dto.auth;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;
}
