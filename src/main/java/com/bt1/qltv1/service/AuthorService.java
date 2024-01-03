package com.bt1.qltv1.service;

import com.bt1.qltv1.dto.author.AuthorRequest;
import com.bt1.qltv1.dto.author.AuthorResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthorService {
    AuthorResponse save(AuthorRequest authorRequest);
}
