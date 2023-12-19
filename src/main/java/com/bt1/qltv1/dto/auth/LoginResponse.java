package com.bt1.qltv1.dto.auth;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private Long id;
    private String username;
    private Set<GrantedAuthority> role;
}
