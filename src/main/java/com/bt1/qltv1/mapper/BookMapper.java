package com.bt1.qltv1.mapper;

import com.bt1.qltv1.dto.author.AuthorRequest;
import com.bt1.qltv1.dto.book.BookRequest;
import com.bt1.qltv1.dto.book.BookResponse;
import com.bt1.qltv1.dto.genre.GenreRequest;
import com.bt1.qltv1.entity.Book;
import com.bt1.qltv1.util.Global;

public class BookMapper {
    public static Book toEntity(BookRequest request){
        if (request.getImage() == null) {
            request.setImage(Global.DEFAULT_IMAGE);
        }
        return Book.builder()
                .isbn(request.getIsbn())
                .title(request.getTitle())
                .description(request.getDescription())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .image(request.getImage())
                .loanFee(request.getLoanFee()).build();
    }
    public static BookResponse tobBookResponse(Book book){
        AuthorRequest author = AuthorRequest.builder()
                .name(book.getAuthorId().getName())
                .id(book.getAuthorId().getId()).build();

        GenreRequest genre = GenreRequest.builder()
                .name(book.getGenreId().getName())
                .id(book.getGenreId().getId()).build();

        return BookResponse.builder()
                .id(book.getId())
                .isbn(book.getIsbn())
                .author(author)
                .genre(genre)
                .image(book.getImage())
                .title(book.getTitle())
                .available(book.isAvailable())
                .price(book.getPrice())
                .quantity(book.getQuantity())
                .description(book.getDescription())
                .loanFee(book.getLoanFee())
                .createdBy(book.getCreatedBy())
                .createdDate(book.getCreatedDate())
                .modifiedBy(book.getModifiedBy())
                .modifiedDate(book.getModifiedDate())
                .build();
    }
}
