package com.bt1.qltv1.repository;

import com.bt1.qltv1.entity.LoanDetail;
import com.bt1.qltv1.enumeration.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface LoanDetailRepository extends JpaRepository<LoanDetail, Long> {

    @Query("SELECT ld FROM LoanDetail ld WHERE ld.loan.user.id = :userId AND ld.loan.status = :status")
    List<LoanDetail> findByUserIdAndStatus(@Param("userId") long userId, @Param("status") LoanStatus status);

    Page<LoanDetail> findByLoanUserIdAndLoanStatus(long userId, LoanStatus status, Pageable pageable);

    Optional<List<LoanDetail>> findAllByLoanId(long loanId);
}
