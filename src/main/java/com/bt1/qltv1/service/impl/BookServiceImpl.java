package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.criteria.BookCriteria;
import com.bt1.qltv1.dto.ListOutputResult;
import com.bt1.qltv1.dto.book.BookRequest;
import com.bt1.qltv1.dto.book.BookResponse;
import com.bt1.qltv1.dto.book.UploadImageResponse;
import com.bt1.qltv1.entity.Author;
import com.bt1.qltv1.entity.Book;
import com.bt1.qltv1.entity.Genre;
import com.bt1.qltv1.exception.BadRequest;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.mapper.BookMapper;
import com.bt1.qltv1.repository.AuthorRepository;
import com.bt1.qltv1.repository.BookRepository;
import com.bt1.qltv1.repository.GenreRepository;
import com.bt1.qltv1.service.BookService;
import com.bt1.qltv1.service.criteria.BookQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Log4j
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookQueryService bookQueryService;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    private Boolean isNumber(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private Boolean isValidNumber(String num) {
        return num != null && !num.isEmpty() && isNumber(num) && Long.parseLong(num) >= 0;
    }

    public Pageable preparePaging(String page, String limit, String order, String sortBy){
        if(Boolean.FALSE.equals(isValidNumber(limit))){
            throw new BadRequest("Invalid limit", "pageable.limit.invalid");
        }
        if (Boolean.FALSE.equals(isValidNumber(page))){
            throw new BadRequest("Invalid page", "pageable.page.invalid");
        }

        Sort sort;
        if(order.equalsIgnoreCase("asc")){
            sort = Sort.by(Sort.Direction.ASC, sortBy);
        }
        else{
            sort = Sort.by(Sort.Direction.DESC, sortBy);
        }

        return PageRequest.of((Integer.parseInt(page) - 1),Integer.parseInt(limit),
                sort);
    }

    public ListOutputResult resultPaging(Page<Book> books){
        if (books.isEmpty()){
            throw new NotFoundException("No result","search.book.notfound");
        }

        List<BookResponse> bookResponses = new ArrayList<>();
        for (Book book : books){
            bookResponses.add(BookMapper.tobBookResponse(book));
        }

        ListOutputResult result = new ListOutputResult(books.getTotalElements(),
                books.getTotalPages(), null,null,bookResponses);

        if(books.hasNext()){
            result.setNextPage((long) books.nextPageable().getPageNumber() + 1);
        }
        if(books.hasPrevious()){
            result.setPreviousPage((long) books.previousPageable().getPageNumber() + 1);
        }

        return result;
    }

    @Override
    public ListOutputResult findAllBook(BookCriteria bookCriteria, String page,
                                        String limit, String order, String sortBy) {
        Pageable pageable = preparePaging(page, limit, order, sortBy);
        Page<Book> bookPage = bookQueryService.findByCriteria(bookCriteria, pageable);
        return resultPaging(bookPage);
    }

    @Override
    public BookResponse save(BookRequest bookRequest) {
        Author author = authorRepository.findById(bookRequest.getAuthorId()).orElseThrow(()->
                new NotFoundException("Not found author with id "+bookRequest.getAuthorId(),
                        "author.id.not-exist"));

        Genre genre = genreRepository.findById(bookRequest.getGenreId()).orElseThrow(()->
                new NotFoundException("Not found genre with id "+bookRequest.getGenreId(),
                        "genre.id.not-exist"));

        Book book;
        if(bookRequest.getId() !=null){
            book = bookRepository.findById(bookRequest.getId()).orElseThrow(()->
                    new NotFoundException("Not found book with id "+bookRequest.getId(),
                            "book.id.not-exist"));

            book.setIsbn(bookRequest.getIsbn());
            book.setTitle(bookRequest.getTitle());
            book.setDescription(bookRequest.getDescription());
            book.setQuantity(bookRequest.getQuantity());
            book.setAvailable(bookRequest.isAvailable());
            book.setPrice(bookRequest.getPrice());
            book.setLoanFee(bookRequest.getLoanFee());
            book.setImage(bookRequest.getImage());
        }
        else {
            Optional<Book> bookByIsbn = bookRepository.findByIsbn(bookRequest.getIsbn());
            if(bookByIsbn.isPresent()){
                throw new BadRequest("Isbn code duplicate, Please retype!",
                        "book.isbn.isbn-existed");
            }

            book = BookMapper.toEntity(bookRequest);
        }
        book.setAuthorId(author);
        book.setGenreId(genre);

        book = bookRepository.save(book);

        return BookMapper.tobBookResponse(book);
    }

    @Override
    public void deleteById(long id) {
        Book bookDeleted = bookRepository.findById(id).orElseThrow(()->
                new NotFoundException("Not found book with id " + id, "book.delete.id-not-exist"));

        bookDeleted.setAvailable(false);
        bookRepository.save(bookDeleted);
    }

    @Override
    public UploadImageResponse uploadImage(long id, MultipartFile image) {
        return null;
    }
}
