package com.bt1.qltv1.dto.returnred;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class BookReturnResponse {
    private Long loanId;
    private Long bookId;
    private String isbn;
    private String title;
    private String image;
    private Integer quantity;
    private BigDecimal fee;
    private LocalDateTime returnDate;
    private LocalDateTime borrowDate;
}
