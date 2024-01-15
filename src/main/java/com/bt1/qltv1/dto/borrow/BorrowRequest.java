package com.bt1.qltv1.dto.borrow;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BorrowRequest {
    private String returnDate;
    private List<BookBorrow> bookBorrowList;
}
