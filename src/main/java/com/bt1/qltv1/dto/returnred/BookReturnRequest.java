package com.bt1.qltv1.dto.returnred;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BookReturnRequest {
    @NotNull(message = "${book.id.null}")
    private Long bookId;

    @Min(value = 1, message = "{book.quantity.minimum}")
    private Integer quantity;
}
