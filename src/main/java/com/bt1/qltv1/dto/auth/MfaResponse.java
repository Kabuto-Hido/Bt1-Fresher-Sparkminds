package com.bt1.qltv1.dto.auth;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MfaResponse {
    private String secretKey;
    private String qrCodeUrl;
}
