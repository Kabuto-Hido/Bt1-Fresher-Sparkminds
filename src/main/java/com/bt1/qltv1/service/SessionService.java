package com.bt1.qltv1.service;

import com.bt1.qltv1.entity.Session;
import com.bt1.qltv1.entity.User;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public interface SessionService {
    void saveSession(Session session, long userId);
    Session findByJti(String jti);
    boolean checkIsBlockSession(String jti);
    boolean checkIsRightRefreshToken(String jti, LocalDateTime time);
    void blockSession(String jti);
}
