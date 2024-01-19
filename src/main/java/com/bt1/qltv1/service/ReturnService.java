package com.bt1.qltv1.service;

import com.bt1.qltv1.dto.ListOutputResult;
import com.bt1.qltv1.dto.returnred.ReturnRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface ReturnService {
    ListOutputResult getBorrowedBook(Pageable pageable);

    void returnBook(ReturnRequest returnRequest);
}
