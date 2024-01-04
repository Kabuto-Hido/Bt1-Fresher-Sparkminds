package com.bt1.qltv1.service;

import com.bt1.qltv1.criteria.BookCriteria;
import com.bt1.qltv1.dto.ListOutputResult;
import com.bt1.qltv1.dto.book.BookRequest;
import com.bt1.qltv1.dto.book.BookResponse;
import com.bt1.qltv1.dto.book.UploadImageResponse;
import com.bt1.qltv1.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface BookService {
    ListOutputResult findAllBook(BookCriteria bookCriteria, String page, String limit,
                                 String order, String sortBy);

    BookResponse save(BookRequest bookRequest);
    void deleteById(long id);
    UploadImageResponse uploadImage(long id, MultipartFile image);
}
