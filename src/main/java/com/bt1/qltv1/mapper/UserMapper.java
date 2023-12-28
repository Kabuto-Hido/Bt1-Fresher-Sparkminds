package com.bt1.qltv1.mapper;

import com.bt1.qltv1.dto.user.ProfileResponse;
import com.bt1.qltv1.entity.Role;
import com.bt1.qltv1.entity.User;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j
public class UserMapper {
    public static ProfileResponse toProfileDto (User user){
        List<String> roles = user.getRoleSet()
                .stream().map(Role::getName).toList();

        return ProfileResponse.builder()
                .modifiedDate(user.getModifiedDate())
                .createdDate(user.getCreatedDate())
                .phone(user.getPhone())
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .status(user.getStatus().name())
                .avatar(user.getAvatar())
                .roles(roles)
                .build();
    }
}
