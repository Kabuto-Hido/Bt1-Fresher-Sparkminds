package com.bt1.qltv1.dto.book;

import com.bt1.qltv1.dto.author.AuthorRequest;
import com.bt1.qltv1.dto.genre.GenreRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class BookRequest {
    private Long id;
    private String isbn;
    @NotBlank(message = "Title can be not null")
    private String title;
    private String image;
    private String description;
    @Builder.Default
    private Integer quantity = 1;
    private boolean available;
    @NotBlank(message = "Price can be not null")
    private Double price;
    @NotBlank(message = "Loan fee can be not null")
    private Double loanFee;
    @NotNull(message = "Author can be not null")
    private Long authorId;
    @NotNull(message = "Genre can be not null")
    private Long genreId;
}
