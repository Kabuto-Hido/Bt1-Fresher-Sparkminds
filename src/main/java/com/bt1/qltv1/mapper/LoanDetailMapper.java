package com.bt1.qltv1.mapper;

import com.bt1.qltv1.dto.borrow.LoanDetailResponse;
import com.bt1.qltv1.entity.LoanDetail;

public class LoanDetailMapper {
    public static LoanDetailResponse toLoanDetailResponse(LoanDetail loanDetail){
        return LoanDetailResponse.builder()
                .bookId(loanDetail.getBook().getId())
                .quantity(loanDetail.getQuantity())
                .unitPrice(loanDetail.getUnitPrice()).build();
    }
}
