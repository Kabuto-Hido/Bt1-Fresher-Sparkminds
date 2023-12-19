package com.bt1.qltv1.service;

import com.bt1.qltv1.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    User findFirstByEmail(String email);
    void save(String email, String password);
    void increaseFailedAttempts(String email, int failedAttempts);
    void lockAccount(User user);
    void unlockAccount(User user);
    String getTimeRemaining(User user);
    void resetFailedAttempts(String email);
}
