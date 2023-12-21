package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.config.Global;
import com.bt1.qltv1.config.UserStatus;
import com.bt1.qltv1.dto.user.ProfileResponse;
import com.bt1.qltv1.entity.Role;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.mapper.UserMapper;
import com.bt1.qltv1.repository.RoleRepository;
import com.bt1.qltv1.repository.UserRepository;
import com.bt1.qltv1.service.AuthService;
import com.bt1.qltv1.service.UserService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Log4j
@Component
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<ProfileResponse> findAllUser() {
        List<User> userList = userRepository.findAll(
                Sort.by(Sort.Direction.DESC, "createdDate"));

        List<ProfileResponse> profileResponses = new ArrayList<>();
        for (User user : userList){
            profileResponses.add(UserMapper.toProfileDto(user));
        }
        return profileResponses;
    }

    @Override
    public User findFirstByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new NotFoundException("Wrong email!!"));
    }

    @Override
    public void save(String email, String password) {
        Optional<User> userByEmail = userRepository.findByEmail(email);
        if (userByEmail.isPresent()) {
            throw new IllegalArgumentException("Email duplicate, Please retype!");
        }
        if (password == null || password.length() <= 6) {
            throw new IllegalArgumentException("Password must be longer than 7 character and can't be null");
        }

        //Nếu không trùng username, encode pwd và lưu vào db user
        Optional<Role> roleUserOptional = roleRepository.findById(Long.parseLong("2")); //Lấy ROLE_USER
        List<Role> roleUserList = new ArrayList<>();
        roleUserOptional.ifPresent(roleUserList::add);
        Set<Role> roleUserSet = new HashSet<>(roleUserList);//ép kiểu role thành set gán cho entity user

        User newUser = User.builder().fullName("Kabuto")
                .status(UserStatus.ACTIVE)
                .password(passwordEncoder.encode(password))
                .email(email)
                .roleSet(roleUserSet).build();

        log.info("New user: " + newUser);

        userRepository.save(newUser);
    }

    @Override
    public void increaseFailedAttempts(String email, int failedAttempt) {
        log.info("login fail attempts "+ (failedAttempt+1));
        userRepository.updateFailedAttempts(email, (failedAttempt + 1));
    }

    @Override
    public void lockAccount(User user) {
        user.setStatus(UserStatus.BLOCK);
        LocalDateTime now = LocalDateTime.now();
        user.setLockTime(now.plusMinutes(Global.LOCK_TIME_DURATION / 60000));
        userRepository.save(user);

        log.info("lock account:" +user.getEmail() + " in time "+user.getLockTime());
    }

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
        LocalDateTime d2 = LocalDateTime.now();

        Duration duration = Duration.between(d1, d2);
        long minuteRemaining = duration.getSeconds() / 60;
        long secondRemaining = duration.getSeconds() % 60;
        return String.format("%sm %ss", minuteRemaining, secondRemaining);
    }

    @Override
    public void resetFailedAttempts(String email) {
        userRepository.updateFailedAttempts(email, 0);
    }

    @Override
    public int enableMfa(String secret) {
        String email = AuthService.GetEmailLoggedIn();
        return userRepository.enableMfa(email, secret);
    }
}
