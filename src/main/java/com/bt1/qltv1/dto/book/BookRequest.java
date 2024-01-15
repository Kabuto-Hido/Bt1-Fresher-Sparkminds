package com.bt1.qltv1.dto.book;

import lombok.*;
import org.hibernate.validator.constraints.ISBN;

import javax.validation.constraints.Min;
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

    @NotNull(message = "{book.title.null}")
    private String title;

    private String image;

    private String description;

    @Builder.Default
    @Min(value = 1, message = "{book.quantity.minimum}")
    private Integer quantity = 1;

    private boolean available;

    @NotNull(message = "{book.price.null}")
    private BigDecimal price;

    @NotNull(message = "{book.loan-fee.null}")
    private BigDecimal loanFee;

    @NotNull(message = "{book.author.null}")
    private Long authorId;

    @NotNull(message = "{book.genre.null}")
    private Long genreId;
}
