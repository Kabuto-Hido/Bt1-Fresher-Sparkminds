package com.bt1.qltv1.service;

import com.bt1.qltv1.criteria.BookCriteria;
import com.bt1.qltv1.dto.ListOutputResult;
import com.bt1.qltv1.dto.book.BookRequest;
import com.bt1.qltv1.dto.book.BookResponse;
import com.bt1.qltv1.repository.BookRepository;
import org.springframework.stereotype.Service;

@Service
public interface BookService {
    ListOutputResult findAllBook(BookCriteria bookCriteria, String page, String limit,
                                 String order, String sortBy);

    BookResponse save(BookRequest bookRequest);
}
