package com.bt1.qltv1.dto.register;

import com.bt1.qltv1.entity.BaseEntity;
import com.bt1.qltv1.enumeration.ActivateMailType;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

public class RegisterRequest extends BaseEntity {
    private String fullname;

    @Email(message = "Please enter the valid email")
    private String email;

    //use library java training
    @Pattern(regexp = "^\\d{10}$")
    private String phone;

//  Min 1 uppercase letter.
//  Min 1 special character.
//  Min 1 number.
//  Min 8 characters.
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[#$@!%&*?])[A-Za-z\\d#$@!%&*?]{8,}$")
    private String password;

    @Enumerated(EnumType.STRING)
    private ActivateMailType mailType = ActivateMailType.LINK;
}
