package com.bt1.qltv1.dto.user;

import com.bt1.qltv1.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)

public class ProfileResponse extends BaseEntity {
    private long id;
    private String fullName;
    private String email;
    private String phone;
    private String avatar;
    private String status;
    private int failedAttempt;
    private boolean mfaEnabled;
    private boolean verifyMail;
    private List<String> roles;

}
