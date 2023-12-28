package com.bt1.qltv1.repository;

import com.bt1.qltv1.enumeration.SessionStatus;
import com.bt1.qltv1.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByJtiAndStatus(String jti, SessionStatus status);
    Optional<Session> findByJtiAndExpiredDate(String jti, LocalDateTime time);
    Optional<Session> findByJti(String jti);
}
