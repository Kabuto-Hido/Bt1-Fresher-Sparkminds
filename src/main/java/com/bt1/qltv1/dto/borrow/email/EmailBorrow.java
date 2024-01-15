package com.bt1.qltv1.dto.borrow.email;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class EmailBorrow {
    private long loanId;
    private String createDate;
    private String totalPrice;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private List<EmailBook> emailBooks;
}
