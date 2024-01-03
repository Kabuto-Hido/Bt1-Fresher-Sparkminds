package com.bt1.qltv1.mapper;

import com.bt1.qltv1.dto.user.ProfileResponse;
import com.bt1.qltv1.dto.user.UserDTO;
import com.bt1.qltv1.entity.Role;
import com.bt1.qltv1.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j
@Component
public class UserMapper {
    public static User toEntity (UserDTO userDTO){
        return User.builder()
                .fullName(userDTO.getFullname())
                .phone(userDTO.getPhone())
                .email(userDTO.getEmail())
                .build();
    }

    public static ProfileResponse toProfileDto (User user){
        List<String> roles = user.getRoleSet()
                .stream().map(Role::getName).toList();

        return ProfileResponse.builder()
                .modifiedDate(user.getModifiedDate())
                .createdDate(user.getCreatedDate())
                .modifiedBy(user.getModifiedBy())
                .createdBy(user.getCreatedBy())
                .phone(user.getPhone())
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .status(user.getStatus().name())
                .avatar(user.getAvatar())
                .verifyMail(user.isVerifyMail())
                .mfaEnabled(user.isMfaEnabled())
                .failedAttempt(user.getFailedAttempt())
                .roles(roles)
                .build();
    }
}
