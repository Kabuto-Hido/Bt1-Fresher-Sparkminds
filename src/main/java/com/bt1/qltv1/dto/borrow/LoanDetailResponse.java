package com.bt1.qltv1.dto.borrow;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LoanDetailResponse {
    private Integer quantity;
    private BigDecimal unitPrice;
    private Long bookId;
}
