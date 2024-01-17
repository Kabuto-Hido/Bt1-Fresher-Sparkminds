package com.bt1.qltv1.dto.book;

import com.bt1.qltv1.dto.author.AuthorRequest;
import com.bt1.qltv1.dto.genre.GenreRequest;
import com.bt1.qltv1.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString

public class BookResponse extends BaseEntity {
    private Long id;
    private String isbn;
    private String title;
    private String image;
    private String description;
    private Integer quantity;
    private boolean available;
    private boolean inStock;
    private BigDecimal price;
    private BigDecimal loanFee;
    private AuthorRequest author;
    private GenreRequest genre;
}
