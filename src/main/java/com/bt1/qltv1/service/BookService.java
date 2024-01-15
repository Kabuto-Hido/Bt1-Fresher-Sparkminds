package com.bt1.qltv1.service;

import com.bt1.qltv1.criteria.BaseCriteria;
import com.bt1.qltv1.criteria.BookCriteria;
import com.bt1.qltv1.dto.ListOutputResult;
import com.bt1.qltv1.dto.book.BookRequest;
import com.bt1.qltv1.dto.book.BookResponse;
import com.bt1.qltv1.dto.book.UploadImageResponse;
import com.bt1.qltv1.repository.BookRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface BookService {
    ListOutputResult findAllBook(BookCriteria bookCriteria, BaseCriteria baseCriteria,
                                 Pageable pageable);

    BookResponse save(BookRequest bookRequest, MultipartFile image);
    void deleteById(long id);

    void importBookByCsv(MultipartFile file);

    UploadImageResponse uploadBookImage(long id, MultipartFile image);

    void deleteBookImage(long id);

    UploadImageResponse getBookImage(long id);
}
