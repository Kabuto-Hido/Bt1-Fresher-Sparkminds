package com.bt1.qltv1.mapper;

import com.bt1.qltv1.dto.borrow.LoanDetailResponse;
import com.bt1.qltv1.dto.returnred.BookReturnResponse;
import com.bt1.qltv1.entity.LoanDetail;
import com.bt1.qltv1.service.ImageService;
import com.bt1.qltv1.util.Global;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

@RequiredArgsConstructor
public class LoanDetailMapper {
    private final ImageService imageService;
    public static LoanDetailResponse toLoanDetailResponse(LoanDetail loanDetail){
        return LoanDetailResponse.builder()
                .bookId(loanDetail.getBook().getId())
                .quantity(loanDetail.getQuantity())
                .unitPrice(loanDetail.getUnitPrice()).build();
    }

    public static BookReturnResponse toBookReturnResponse(LoanDetail loanDetail){
        Path imagePath = Path.of(Global.BOOK_DIR, loanDetail.getBook().getImage());
        String urlImage = imagePath.toAbsolutePath().toString();

        return BookReturnResponse.builder()
                .loanId(loanDetail.getLoan().getId())
                .bookId(loanDetail.getBook().getId())
                .isbn(loanDetail.getBook().getIsbn())
                .title(loanDetail.getBook().getTitle())
                .image(urlImage)
                .borrowDate(loanDetail.getLoan().getCreatedDate())
                .returnDate(loanDetail.getLoan().getReturnDate())
                .quantity(loanDetail.getQuantity())
                .fee(loanDetail.getUnitPrice()).build();
    }
}
