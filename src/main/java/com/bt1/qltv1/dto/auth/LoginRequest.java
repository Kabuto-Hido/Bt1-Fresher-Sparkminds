package com.bt1.qltv1.dto.auth;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class LoginRequest {
    private String email;
    private String password;
}
