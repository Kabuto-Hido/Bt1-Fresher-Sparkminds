package com.bt1.qltv1.repository;

import com.bt1.qltv1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    @Modifying
    @Transactional
    @Query("update User u set u.failedAttempt = ?2 where u.email = ?1")
    void updateFailedAttempts(String email, int failedAttempts);

    @Modifying
    @Transactional
    @Query("update User u set u.secret = ?2, u.mfaEnabled = true where u.email = ?1")
    int enableMfa(String email, String secret);

    Optional<User> findFirstByEmailAndVerifyMail(String email, boolean isVerify);
}
