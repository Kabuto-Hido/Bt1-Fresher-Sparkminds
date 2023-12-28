package com.bt1.qltv1.controller.admin;

import com.bt1.qltv1.dto.user.ProfileResponse;
import com.bt1.qltv1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class UserManagementController {
    private final UserService userService;

    @GetMapping("/user-management/users")
    public ResponseEntity<List<ProfileResponse>> getListUser(){
        return ResponseEntity.ok(userService.findAllUser());
    }

}
