package com.bt1.qltv1.mapper;

import com.bt1.qltv1.dto.borrow.BorrowResponse;
import com.bt1.qltv1.dto.borrow.LoanDetailResponse;
import com.bt1.qltv1.entity.Loan;
import com.bt1.qltv1.entity.LoanDetail;

import java.util.ArrayList;
import java.util.List;

public class LoanMapper {
    public static BorrowResponse toBorrowResponse(Loan loan){
        List<LoanDetailResponse> loanDetailResponses = new ArrayList<>();
        for(LoanDetail ld : loan.getListLoanDetail()){
            loanDetailResponses.add(LoanDetailMapper.toLoanDetailResponse(ld));
        }

        return BorrowResponse.builder()
                .idLoan(loan.getId())
                .returnDate(loan.getReturnDate())
                .totalPrice(loan.getTotalPrice())
                .status(loan.getStatus())
                .paid(loan.isPaid())
                .userId(loan.getUser().getId())
                .loanDetails(loanDetailResponses)
                .createdDate(loan.getCreatedDate())
                .modifiedBy(loan.getModifiedBy())
                .modifiedDate(loan.getModifiedDate())
                .createdBy(loan.getCreatedBy())
                .build();
    }
}
