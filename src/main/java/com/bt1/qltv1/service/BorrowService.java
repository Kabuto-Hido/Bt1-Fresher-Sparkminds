package com.bt1.qltv1.service;

import com.bt1.qltv1.criteria.BookCriteria;
import com.bt1.qltv1.dto.ListOutputResult;
import com.bt1.qltv1.dto.borrow.BorrowRequest;
import com.bt1.qltv1.dto.borrow.BorrowResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface BorrowService {
    ListOutputResult getBookCanBorrow(BookCriteria bookCriteria, Pageable pageable);

    BorrowResponse borrowBook(BorrowRequest borrowRequest);

    void confirmBorrowBook(long loanId);
}
