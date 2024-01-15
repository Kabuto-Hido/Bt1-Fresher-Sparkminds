package com.bt1.qltv1.dto.register;

import com.bt1.qltv1.entity.BaseEntity;
import com.bt1.qltv1.validation.Phone;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

public class RegisterRequest extends BaseEntity {
    @Size(min = 1, max = 100, message = "{user.full-name.invalid}")
    private String fullname;

    @Email(message = "{user.email.invalid}")
    private String email;

    //use library java
    @Phone
    private String phone;

    //  Min 1 uppercase letter.
//  Min 1 special character.
//  Min 1 number.
//  Min 8 characters.
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[#$@!%&*?])[A-Za-z\\d#$@!%&*?]{8,}$",
            message = "{user.password.weak}")
    private String password;
}
