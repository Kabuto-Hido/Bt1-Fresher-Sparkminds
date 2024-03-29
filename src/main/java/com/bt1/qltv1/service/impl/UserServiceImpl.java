package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.criteria.BaseCriteria;
import com.bt1.qltv1.criteria.UserCriteria;
import com.bt1.qltv1.dto.ListOutputResult;
import com.bt1.qltv1.dto.book.UploadImageResponse;
import com.bt1.qltv1.dto.user.ProfileResponse;
import com.bt1.qltv1.dto.user.UserDTO;
import com.bt1.qltv1.entity.Role;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.enumeration.UserStatus;
import com.bt1.qltv1.exception.BadRequest;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.mapper.UserMapper;
import com.bt1.qltv1.repository.RoleRepository;
import com.bt1.qltv1.repository.UserRepository;
import com.bt1.qltv1.service.FileService;
import com.bt1.qltv1.service.ImageService;
import com.bt1.qltv1.service.UserService;
import com.bt1.qltv1.service.criteria.UserQueryService;
import com.bt1.qltv1.util.Global;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Log4j
@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserQueryService userQueryService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;
    private final ImageService imageService;

    public ListOutputResult resultPaging(Page<User> users) {
        //mapper to profile response
        if (users.isEmpty()) {
            throw new NotFoundException("No result", "search.user.notfound");
        }

        List<ProfileResponse> profileResponses = new ArrayList<>();
        for (User user : users) {
            profileResponses.add(UserMapper.toProfileDto(user));
        }

        ListOutputResult result = new ListOutputResult(users.getTotalElements(),
                users.getTotalPages(), null, null, profileResponses);

        if (users.hasNext()) {
            result.setNextPage((long) users.nextPageable().getPageNumber() + 1);
        }
        if (users.hasPrevious()) {
            result.setPreviousPage((long) users.previousPageable().getPageNumber() + 1);
        }

        return result;
    }


    @Override
    public ProfileResponse findById(long id) {
        User userById = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Not found user with id " + id, "find-user.id.not-exist"));
        return UserMapper.toProfileDto(userById);
    }

    @Override
    public ListOutputResult findAllUser(UserCriteria userCriteria, BaseCriteria baseCriteria, Pageable pageable) {
        Page<User> userPage = userQueryService.findByCriteria(userCriteria, baseCriteria, pageable);

        return resultPaging(userPage);
    }

    @Transactional
    @Override
    public void importUserByCsv(MultipartFile file) {
        try {
            List<String[]> records = fileService.readCsvFileWithoutHeader(file, User.class);

            List<Role> roleUserList = new ArrayList<>();
            Optional<Role> roleUserOptional = roleRepository.findById(2L);
            roleUserOptional.ifPresent(roleUserList::add);
            Set<Role> roleUserSet = new HashSet<>(roleUserList);

            String defaultPass = passwordEncoder.encode(Global.NEW_RESET_PASSWORD);

            List<User> userImportList = new ArrayList<>();

            for (String[] line : records) {
                checkEmailOrPhoneValid(line[1], line[2]);
                // Process each record
                User newUser = User.builder()
                        .fullName(line[0])
                        .email(line[1])
                        .phone(line[2])
                        .password(defaultPass)
                        .roleSet(roleUserSet).build();

                userImportList.add(newUser);
            }

            userRepository.saveAll(userImportList);
        } catch (IOException | CsvException e) {
            log.error("file error: " + e.getMessage());
            throw new BadRequest(e.getMessage(), "");
        } finally {
            log.info("Import user by csv file done!");
        }
    }

    public void checkEmailOrPhoneValid(String email, String phone) {
        Optional<User> userByEmail = userRepository.findByEmail(email);
        if (userByEmail.isPresent()) {
            throw new BadRequest("Email duplicate, Please retype!",
                    "user.email.email-existed");
        }

        Optional<User> userByPhone = userRepository.findByPhone(phone);
        if (userByPhone.isPresent()) {
            throw new BadRequest("Phone duplicate, Please retype!", "user.phone.phone-existed");
        }
    }

    @Override
    public ProfileResponse save(UserDTO dto) {
        List<Role> roleUserList = new ArrayList<>();
        Optional<Role> roleUserOptional = roleRepository.findById(2L);
        roleUserOptional.ifPresent(roleUserList::add);
        Set<Role> roleUserSet = new HashSet<>(roleUserList);

        User user;

        //update
        if (dto.getId() != null) {
            user = userRepository.findById(dto.getId()).orElseThrow(() ->
                    new NotFoundException("User not exist!", "user.id.invalid"));

            if(!user.getEmail().equals(dto.getEmail()) || !user.getPhone().equals(dto.getPhone())){
                checkEmailOrPhoneValid(dto.getEmail(), dto.getPhone());
                user.setEmail(dto.getEmail());
                user.setPhone(dto.getPhone());
            }

            user.setFullName(dto.getFullname());
            user.setStatus(dto.getStatus());
            user.setMfaEnabled(dto.isMfaEnabled());
            user.setVerifyMail(dto.isVerifyMail());
        } else {
            checkEmailOrPhoneValid(dto.getEmail(), dto.getPhone());

            user = UserMapper.toEntity(dto);
            user.setPassword(passwordEncoder.encode(Global.NEW_RESET_PASSWORD));
        }

        user.setRoleSet(roleUserSet);
        user = userRepository.save(user);

        return UserMapper.toProfileDto(user);
    }

    @Override
    public void deleteUser(long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Not found user with id " + id, "user.delete.id-not-exist"));

        //update status
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
    }

    @Override
    public User findFirstByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new NotFoundException("Wrong email!!", "user.email.not-exist"));
    }

    @Override
    public UploadImageResponse updateAvatar(MultipartFile avatar) {
        try {
            String email = UserDetailsServiceImpl.getEmailLoggedIn();

            User user = findFirstByEmail(email);

            String fileName = user.getId() + "_" + user.getFullName();
            Path filePath = imageService.saveImageToStorage(fileName, Global.AVATAR_DIR, avatar);

            user.setAvatar(filePath.getFileName().toString());
            userRepository.save(user);

            return UploadImageResponse.builder()
                    .url(filePath.toAbsolutePath().toString()).build();

        } catch (Exception e) {
            log.error("An error occurred in updateAvatar method", e);
            throw new BadRequest("An error occurred in updateAvatar method",
                    "user.avatar.fail");
        } finally {
            log.info("Update avatar finished ");
        }


    }

    @Override
    public void deleteAvatar() {
        String email = UserDetailsServiceImpl.getEmailLoggedIn();
        User user = findFirstByEmail(email);

        imageService.deleteImage(user.getAvatar(), Global.AVATAR_DIR);

        user.setAvatar(Global.DEFAULT_AVATAR);
        userRepository.save(user);
    }

    @Override
    public UploadImageResponse getAvatar() {
        String email = UserDetailsServiceImpl.getEmailLoggedIn();
        User user = findFirstByEmail(email);

        String url = imageService.getDirImage(user.getAvatar(), Global.AVATAR_DIR);

        return UploadImageResponse.builder().url(url).build();
    }

    @Override
    public ProfileResponse getProfile() {
        String email = UserDetailsServiceImpl.getEmailLoggedIn();
        User user = findFirstByEmail(email);
        return UserMapper.toProfileDto(user);
    }

}
