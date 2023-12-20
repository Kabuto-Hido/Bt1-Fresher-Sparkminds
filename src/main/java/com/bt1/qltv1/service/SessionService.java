package com.bt1.qltv1.service;

import com.bt1.qltv1.entity.Session;
import com.bt1.qltv1.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface SessionService {
    void saveSession(Session session, long userId);
    Session findByJti(String jti);
}
