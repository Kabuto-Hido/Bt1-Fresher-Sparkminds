package com.bt1.qltv1.controller.user;

import com.bt1.qltv1.dto.SuccessResponseDTO;
import com.bt1.qltv1.dto.book.UploadImageResponse;
import com.bt1.qltv1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;
    @PutMapping("/profile/avatar")
    public ResponseEntity<UploadImageResponse> uploadAvatar(@RequestParam MultipartFile avatar) {
        return ResponseEntity.ok(userService.updateAvatar(avatar));
    }

    @DeleteMapping("/profile/avatar")
    public ResponseEntity<SuccessResponseDTO> deleteAvatar() {
        userService.deleteAvatar();
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Delete image book successful!"));
    }

    @GetMapping("/profile/avatar")
    public ResponseEntity<UploadImageResponse> reviewAvatar(){
        return ResponseEntity.ok(userService.getAvatar());
    }
}
