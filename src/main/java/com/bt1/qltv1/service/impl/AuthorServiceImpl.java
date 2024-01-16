package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.dto.ListOutputResult;
import com.bt1.qltv1.dto.author.AuthorRequest;
import com.bt1.qltv1.dto.author.AuthorResponse;
import com.bt1.qltv1.entity.Author;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.mapper.AuthorMapper;
import com.bt1.qltv1.repository.AuthorRepository;
import com.bt1.qltv1.service.AuthorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public ListOutputResult getAllAuthor(Pageable pageable) {
        Page<Author> authorPage = authorRepository.findAll(pageable);
        return resultPaging(authorPage);
    }

    public ListOutputResult resultPaging(Page<Author> authors) {
        if (authors.isEmpty()) {
            throw new NotFoundException("No result", "get-all.author.notfound");
        }

        List<AuthorResponse> authorResponses = new ArrayList<>();
        for (Author author : authors) {
            authorResponses.add(AuthorMapper.toAuthorResponse(author));
        }

        ListOutputResult authorResult = new ListOutputResult(authors.getTotalElements(),
                authors.getTotalPages(), null, null, authorResponses);

        if (authors.hasNext()) {
            authorResult.setNextPage((long) authors.nextPageable().getPageNumber() + 1);
        }
        if (authors.hasPrevious()) {
            authorResult.setPreviousPage((long) authors.previousPageable().getPageNumber() + 1);
        }

        return authorResult;
    }
}
