package com.bt1.qltv1.dto.borrow;

import com.bt1.qltv1.entity.BaseEntity;
import com.bt1.qltv1.enumeration.LoanStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class BorrowResponse extends BaseEntity {
    private Long idLoan;
    private LocalDateTime returnDate;
    private BigDecimal totalPrice;
    private LoanStatus status;
    private boolean paid;
    private Long userId;
    private List<LoanDetailResponse> loanDetails;
}
