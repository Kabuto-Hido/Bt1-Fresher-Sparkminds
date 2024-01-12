package com.bt1.qltv1.repository;

import com.bt1.qltv1.entity.Account;
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
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("update Account a set a.failedAttempt = ?2 where a.email = ?1")
    void updateFailedAttempts(String email, int failedAttempts);

    @Modifying
    @Transactional
    @Query("update Account a set a.verifyMail = true where a.email = ?1")
    void activateAccount(String email);

    Optional<Account> findFirstByEmailAndVerifyMail(String email, boolean isVerify);
}
