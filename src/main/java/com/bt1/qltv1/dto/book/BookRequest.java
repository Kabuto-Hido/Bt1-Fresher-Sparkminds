package com.bt1.qltv1.dto.book;

import lombok.*;
import org.hibernate.validator.constraints.ISBN;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class BookRequest {
    private Long id;
    @ISBN
    private String isbn;
    @NotBlank(message = "Title can be not null")
    private String title;
    private String image;
    private String description;
    @Builder.Default
    private Integer quantity = 1;
    private boolean available;

    @NotNull(message = "Price can be not null")
    private BigDecimal price;

    @NotNull(message = "Loan fee can be not null")
    private BigDecimal loanFee;

    @NotNull(message = "Author can be not null")
    private Long authorId;
    @NotNull(message = "Genre can be not null")
    private Long genreId;
}
