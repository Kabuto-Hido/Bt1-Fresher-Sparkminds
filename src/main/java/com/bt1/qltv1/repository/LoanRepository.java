package com.bt1.qltv1.repository;

import com.bt1.qltv1.entity.Loan;
import com.bt1.qltv1.enumeration.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface LoanRepository extends JpaRepository<Loan, Long> {
    Optional<Loan> findByUserIdAndStatus(long userId, LoanStatus status);

    @Transactional
    @Modifying
    @Query("update Loan l set l.totalPrice = ?1 where l.id = ?2")
    void updateTotalPrice(BigDecimal totalPrice, long id);
}
