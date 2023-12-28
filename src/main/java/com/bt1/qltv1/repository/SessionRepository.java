package com.bt1.qltv1.repository;

import com.bt1.qltv1.entity.Session;
import com.bt1.qltv1.enumeration.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByJtiAndStatus(String jti, SessionStatus status);
    Optional<Session> findByJtiAndExpiredDate(String jti, LocalDateTime time);
    Optional<Session> findByJti(String jti);
    @Modifying
    @Transactional
    @Query("update Session s set s.status = 'BLOCK' where s.userId.id = ?1")
    void blockAllSessionByUserId(long userId);
}
