package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.config.SessionStatus;
import com.bt1.qltv1.entity.Session;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.repository.SessionRepository;
import com.bt1.qltv1.repository.UserRepository;
import com.bt1.qltv1.service.SessionService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Log4j
@Component
public class SessionServiceImpl implements SessionService {
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void saveSession(Session session, long userId) {
        log.info("Save session: "+userId);
        User user = userRepository.findById(userId).orElseThrow(()-> new NotFoundException(""));

        session.setUserId(user);
        sessionRepository.save(session);
    }

    @Override
    public Session findByJti(String jti) {

        Session session = sessionRepository.findByJti(jti)
                .orElseThrow(() -> new NotFoundException("Not found user session with jti "+jti));

        log.info("Find session with jti: "+ session);
        return session;
    }

    @Override
    public boolean checkIsBlockSession(String jti) {
        Session session = sessionRepository.findByJtiAndStatus(jti,SessionStatus.BLOCK);
        return session != null;
    }
}
