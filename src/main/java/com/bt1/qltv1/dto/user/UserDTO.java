package com.bt1.qltv1.dto.user;

import com.bt1.qltv1.entity.BaseEntity;
import com.bt1.qltv1.enumeration.UserStatus;
import com.bt1.qltv1.validation.Phone;
import lombok.*;
import lombok.experimental.SuperBuilder;
import net.bytebuddy.implementation.bind.annotation.Super;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO extends BaseEntity {
    private Long id;
    @Size(min = 3, max = 50, message = "{user.full-name.invalid}")
    private String fullname;
    @Email(message = "{user.email.invalid}")
    private String email;
    @Phone
    private String phone;
    private String avatar;
    private String password;
    @Builder.Default
    private int failedAttempt = 0;
    private LocalDateTime lockTime;
    @Builder.Default
    private boolean mfaEnabled = false;
    private String secret;
    @Builder.Default
    private boolean verifyMail = false;
    private String otp;
    private LocalDateTime otpExpired;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;
    private List<String> roles;
}
