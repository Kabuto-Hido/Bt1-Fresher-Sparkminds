package com.bt1.qltv1.repository;

import com.bt1.qltv1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Modifying
    @Transactional
    @Query("update User u set u.failedAttempt = ?2 where u.email = ?1")
    void updateFailedAttempts(String email, int failedAttempts);

    @Modifying
    @Transactional
    @Query("update User u set u.secret = ?2, u.mfaEnabled = true where u.email = ?1")
    int enableMfa(String email, String secret);

    @Modifying
    @Transactional
    @Query("update User u set u.verifyMail = true, u.otp = null, u.otpExpired = null where u.email = ?1")
    void activateAccount(String email);

    Optional<User> findFirstByEmailAndVerifyMail(String email, boolean isVerify);
}
