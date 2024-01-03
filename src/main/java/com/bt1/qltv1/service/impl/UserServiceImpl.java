package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.criteria.UserCriteria;
import com.bt1.qltv1.dto.ListOutputResult;
import com.bt1.qltv1.dto.mfa.VerifyMfaRequest;
import com.bt1.qltv1.dto.user.ProfileResponse;
import com.bt1.qltv1.dto.user.UserDTO;
import com.bt1.qltv1.entity.Role;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.enumeration.UserStatus;
import com.bt1.qltv1.exception.BadRequest;
import com.bt1.qltv1.exception.MfaException;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.mapper.UserMapper;
import com.bt1.qltv1.repository.RoleRepository;
import com.bt1.qltv1.repository.UserRepository;
import com.bt1.qltv1.service.FileService;
import com.bt1.qltv1.service.MfaService;
import com.bt1.qltv1.service.UserService;
import com.bt1.qltv1.service.criteria.UserQueryService;
import com.bt1.qltv1.util.ApplicationUser;
import com.bt1.qltv1.util.Global;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Log4j
@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MfaService mfaService;
    private final UserQueryService userQueryService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;

    private Boolean isNumber(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private Boolean isValidNumber(String num) {
        return num != null && !num.isEmpty() && isNumber(num) && Long.parseLong(num) >= 0;
    }

    public Pageable preparePaging(String page, String limit, String order, String sortBy){
        if(Boolean.FALSE.equals(isValidNumber(limit))){
            throw new BadRequest("Invalid limit", "pageable.limit.invalid");
        }
        if (Boolean.FALSE.equals(isValidNumber(page))){
            throw new BadRequest("Invalid page", "pageable.page.invalid");
        }

        Sort sort;
        if(order.equalsIgnoreCase("asc")){
            sort = Sort.by(Sort.Direction.ASC, sortBy);
        }
        else{
            sort = Sort.by(Sort.Direction.DESC, sortBy);
        }

        return PageRequest.of((Integer.parseInt(page) - 1),Integer.parseInt(limit),
                sort);
    }

    public ListOutputResult resultPaging(Page<User> users){
        //mapper to profile response
        if (users.isEmpty()){
            throw new NotFoundException("No result","search.user.notfound");
        }

        List<ProfileResponse> profileResponses = new ArrayList<>();
        for (User user : users){
            profileResponses.add(UserMapper.toProfileDto(user));
        }

        ListOutputResult result = new ListOutputResult(users.getTotalElements(),
                users.getTotalPages(),null,null,profileResponses);

        if(users.hasNext()){
            result.setNextPage((long) users.nextPageable().getPageNumber() + 1);
        }
        if(users.hasPrevious()){
            result.setPreviousPage((long) users.previousPageable().getPageNumber() + 1);
        }

        return result;
    }


    @Override
    public ListOutputResult findAllUser(UserCriteria userCriteria, String page, String limit,
                                             String order, String sortBy) {
        Pageable pageable = preparePaging(page, limit, order, sortBy);

        Page<User> userPage = userQueryService.findByCriteria(userCriteria, pageable);

        return resultPaging(userPage);
    }

    @Override
    public void importUserByCsv(MultipartFile file) {
        try {
            List<String[]> records = fileService.readCsvFileWithoutHeader(file);

            List<Role> roleUserList = new ArrayList<>();
            Optional<Role> roleUserOptional = roleRepository.findById(2L);
            roleUserOptional.ifPresent(roleUserList::add);
            Set<Role> roleUserSet = new HashSet<>(roleUserList);

            String defaultPass = passwordEncoder.encode(Global.NEW_RESET_PASSWORD);

            List<User> userImportList = new ArrayList<>();

            for (String[] line : records) {
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
            log.error("file error: "+ e.getMessage());
            throw new BadRequest(e.getMessage(),"");
        }
        finally {
            log.info("Import user by csv file done!");
        }
    }

    public void checkEmailOrPhoneValid(String email, String phone){
        Optional<User> userByEmail = userRepository.findByEmail(email);
        if (userByEmail.isPresent()) {
            throw new BadRequest("Email duplicate, Please retype!", "user.email.email-existed");
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
        if(dto.getId() != null){
            user = userRepository.findById(dto.getId()).orElseThrow(()->
                    new NotFoundException("User not exist!","user.id.invalid"));

            user.setFullName(dto.getFullname());
            user.setPhone(dto.getPhone());
            user.setEmail(dto.getEmail());
            user.setStatus(dto.getStatus());
            user.setMfaEnabled(dto.isMfaEnabled());
            user.setVerifyMail(dto.isVerifyMail());
        }
        else {
            checkEmailOrPhoneValid(dto.getEmail(),dto.getPhone());

            user = UserMapper.toEntity(dto);
            user.setPassword(passwordEncoder.encode(Global.NEW_RESET_PASSWORD));
        }

        user.setRoleSet(roleUserSet);
        user = userRepository.save(user);

        return UserMapper.toProfileDto(user);
    }

    @Override
    public void deleteUser(long id) {
        User user = userRepository.findById(id).orElseThrow(()->
                new NotFoundException("Not found user with id " + id, "user.delete.id-not-exist"));

        //update status
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
    }

    @Override
    public User findFirstByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new NotFoundException("Wrong email!!","user.email.not-exist"));
    }

    @Override
    public void increaseFailedAttempts(String email, int failedAttempt) {
        log.info("login fail attempts "+ (failedAttempt+1));
        userRepository.updateFailedAttempts(email, (failedAttempt + 1));
    }

    @Override
    public void lockAccount(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lockTime = now.plusMinutes((Global.LOCK_TIME_DURATION / 60000));
        user.setLockTime(lockTime);
        user.setStatus(UserStatus.BLOCK);
        log.info("lock time "+ lockTime);
        userRepository.save(user);

        log.info("lock account:" +user.getEmail() + " in time "+user.getLockTime());
    }

    //@Transactional(readOnly = true)
    @Override
    public void unlockAccount(User user) {
        user.setStatus(UserStatus.ACTIVE);
        user.setLockTime(null);
        user.setFailedAttempt(0);

        userRepository.save(user);
        log.info("User after unlock: "+ user);
    }

    @Override
    public String getTimeRemaining(User user) {
        LocalDateTime d1 = user.getLockTime();

        if(d1 == null){
            return null;
        }

        LocalDateTime d2 = LocalDateTime.now();

        Duration duration = Duration.between(d2, d1);
        long minuteRemaining = duration.getSeconds() / 60;
        long secondRemaining = duration.getSeconds() % 60;
        return String.format("%sm %ss", minuteRemaining, secondRemaining);
    }

    @Override
    public void resetFailedAttempts(String email) {
        userRepository.updateFailedAttempts(email, 0);
    }

    @Override
    public void enableMfa(VerifyMfaRequest request) {
        try {
            String email = UserDetailsServiceImpl.getEmailLoggedIn();

            if(!mfaService.verifyOtp(request.getSecret(),request.getCode())){
                throw new MfaException("Invalid MFA code","mfa.code.invalid");
            }

            User user = findFirstByEmail(email);

            user.setMfaEnabled(true);
            user.setSecret(request.getSecret());

            userRepository.save(user);
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        finally {
            log.info("Enable MFA success");
        }
    }

    @Transactional
    @Override
    public void disableMfa() {
        try {
            String email = UserDetailsServiceImpl.getEmailLoggedIn();
            User user = findFirstByEmail(email);

            user.setMfaEnabled(false);
            user.setSecret(null);

            userRepository.save(user);
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        finally {
            log.info("Disable MFA success");
        }

    }

    @Override
    public void updateAvatar() {
        try {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((ApplicationUser)principal).getUsername();

        User user = findFirstByEmail(email);
        user.setAvatar(Global.DEFAULT_AVATAR);
        userRepository.save(user);

    } catch (Exception e) {
        log.error("An error occurred in updateAvatar method", e);
        throw new BadRequest("An error occurred in updateAvatar method",
                "user.avatar.fail");
    }
        log.info("updateAvatar method finished ");
    }

}
