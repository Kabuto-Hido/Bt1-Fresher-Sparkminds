package com.bt1.qltv1.dto.user;

import com.bt1.qltv1.validation.Phone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VerifySms {
    @Phone
    private String phone;
    @Pattern(regexp = "^[0-9]{6}$",message = "{user.otp.invalid}")
    private String otp;
}
