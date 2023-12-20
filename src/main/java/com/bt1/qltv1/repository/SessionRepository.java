package com.bt1.qltv1.repository;

import com.bt1.qltv1.config.SessionStatus;
import com.bt1.qltv1.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface SessionRepository extends JpaRepository<Session, Long> {
    Session findByJtiAndStatus(String jti, SessionStatus status);
    Optional<Session> findByJti(String jti);
}
