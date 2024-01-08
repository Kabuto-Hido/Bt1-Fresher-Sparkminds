package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.dto.author.AuthorRequest;
import com.bt1.qltv1.dto.author.AuthorResponse;
import com.bt1.qltv1.entity.Author;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.mapper.AuthorMapper;
import com.bt1.qltv1.repository.AuthorRepository;
import com.bt1.qltv1.service.AuthorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

@Component
@Log4j
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    @Override
    public AuthorResponse save(AuthorRequest authorRequest) {
        Author author = new Author();
        if(authorRequest.getId() != null){
            author = authorRepository.findById(authorRequest.getId()).orElseThrow(()->
                    new NotFoundException("Not found author with id "+authorRequest.getId(),
                            "author.id.not-exist"));
        }
        author.setName(authorRequest.getName());

        author = authorRepository.save(author);
        return AuthorMapper.toAuthorResponse(author);
    }
}
