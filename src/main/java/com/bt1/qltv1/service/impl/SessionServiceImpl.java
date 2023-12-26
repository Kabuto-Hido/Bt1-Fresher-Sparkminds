package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.enumeration.SessionStatus;
import com.bt1.qltv1.entity.Session;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.exception.AuthException;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.repository.SessionRepository;
import com.bt1.qltv1.repository.UserRepository;
import com.bt1.qltv1.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Log4j
@Component
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

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
        Optional<Session> session = sessionRepository.findByJtiAndStatus(jti,SessionStatus.BLOCK);
        if(session.isEmpty()){
            throw new AuthException("Not found user session");
        }
        return true;
    }

    @Override
    public boolean checkIsRightRefreshToken(String jti, LocalDateTime time) {
        Optional<Session> session = sessionRepository.findByJtiAndExpiredDate(jti,time);
        return session.isPresent();
    }

    @Override
    public void blockSession(String jti) {
        try {
            Session blockSession = findByJti(jti);
            blockSession.setStatus(SessionStatus.BLOCK);

            sessionRepository.save(blockSession);
        }catch (Exception ex){
            log.error(ex.getMessage());
        }

    }
}
