package com.bt1.qltv1.dto.mfa;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

public class MfaResponse {
    private String secretKey;
    private String qrCodeUrl;
}
