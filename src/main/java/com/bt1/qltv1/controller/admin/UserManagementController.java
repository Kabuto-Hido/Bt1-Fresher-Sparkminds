package com.bt1.qltv1.controller.admin;

import com.bt1.qltv1.criteria.BaseCriteria;
import com.bt1.qltv1.criteria.UserCriteria;
import com.bt1.qltv1.dto.ListOutputResult;
import com.bt1.qltv1.dto.SuccessResponseDTO;
import com.bt1.qltv1.dto.user.ProfileResponse;
import com.bt1.qltv1.dto.user.UserDTO;
import com.bt1.qltv1.service.UserService;
import com.bt1.qltv1.util.Global;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/user-management")
public class UserManagementController {
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<ListOutputResult> getAllUser(UserCriteria userCriteria,
                                                       BaseCriteria baseCriteria,
                                                       @PageableDefault(sort = Global.DEFAULT_SORT_BY,
                                                               direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userService.findAllUser(userCriteria, baseCriteria, pageable));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ProfileResponse> getUserById(@NotNull(message = "Id can be not null")
                                                       @PathVariable long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping("/users")
    public ResponseEntity<SuccessResponseDTO> createNewUser(@Valid @RequestBody UserDTO request) {
        userService.save(request);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Add new user successful!"));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ProfileResponse> updateUser(@PathVariable long id,
                                                      @Valid @RequestBody UserDTO request) {
        request.setId(id);
        return ResponseEntity.ok(userService.save(request));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<SuccessResponseDTO> deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Delete user successful!"));
    }

    @PostMapping("/users/upload-csv")
    public ResponseEntity<SuccessResponseDTO> insertNewUserByFile(@RequestParam MultipartFile file) {
        userService.importUserByCsv(file);
        return ResponseEntity.ok(new SuccessResponseDTO(
                HttpStatus.CREATED,
                "Import user in file " + file.getOriginalFilename() + " successful"));
    }
}
