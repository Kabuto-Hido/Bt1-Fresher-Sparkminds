package com.bt1.qltv1.dto.register;

import com.bt1.qltv1.enumeration.ActivateMailType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SendMailRequest {
    @Email(message = "Please enter the valid email")
    private String email;
    @Enumerated(EnumType.STRING)
    private ActivateMailType mailType = ActivateMailType.LINK;
}
