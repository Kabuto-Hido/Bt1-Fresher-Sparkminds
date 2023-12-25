package com.bt1.qltv1.controller;

import com.bt1.qltv1.dto.user.ProfileResponse;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.exception.TokenException;
import com.bt1.qltv1.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@Log4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/get-all")
    public ResponseEntity<List<ProfileResponse>> getListUser(){
        return ResponseEntity.ok(userService.findAllUser());
    }

    @PutMapping("/changeProfile")
    public ResponseEntity<String> changeProfileUser() {
        userService.updateAvatar();
        return ResponseEntity.ok("success");
    }
}
