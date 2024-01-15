package com.bt1.qltv1.repository;

import com.bt1.qltv1.entity.Otp;
import com.bt1.qltv1.enumeration.OtpType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByAccountIdAndType(long accountId, OtpType type);

    @Modifying
    @Transactional
    @Query("update Otp o set o.code = null, o.otpExpired = null where o.account.id = ?1")
    void reset(long id);
}
