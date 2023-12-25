package com.bt1.qltv1.dto.auth;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

public class LoginRequest {
    private String email;
    private String password;
    private String code;
}
