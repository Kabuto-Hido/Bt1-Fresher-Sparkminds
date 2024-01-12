package com.bt1.qltv1.dto.borrow;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BookBorrow {
    @NotNull(message = "{book.id.null}")
    private Long id;

    @Builder.Default
    @Min(value = 1, message = "{book.quantity.minimum}")
    private Integer quantity = 1;
}
