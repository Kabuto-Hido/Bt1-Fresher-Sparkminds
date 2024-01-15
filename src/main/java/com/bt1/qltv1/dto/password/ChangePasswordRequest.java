package com.bt1.qltv1.dto.password;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePasswordRequest {
    @NotNull(message = "{user.password.null}")
    private String oldPassword;
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[#$@!%&*?])[A-Za-z\\d#$@!%&*?]{8,}$",
            message = "{user.password.weak}")
    @NotNull(message = "{user.new-password.null}")
    private String newPassword;
}
