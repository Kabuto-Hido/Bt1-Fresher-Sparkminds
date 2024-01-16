package com.bt1.qltv1.controller.user;

import com.bt1.qltv1.criteria.BookCriteria;
import com.bt1.qltv1.dto.ListOutputResult;
import com.bt1.qltv1.dto.SuccessResponseDTO;
import com.bt1.qltv1.dto.book.BookResponse;
import com.bt1.qltv1.dto.borrow.BorrowRequest;
import com.bt1.qltv1.dto.borrow.BorrowResponse;
import com.bt1.qltv1.service.BorrowService;
import com.bt1.qltv1.util.Global;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class BorrowController {
    private final BorrowService borrowService;

    @GetMapping("/book/borrow")
    public ResponseEntity<ListOutputResult> getBookCanBorrow(BookCriteria bookCriteria,
                                                             @PageableDefault(sort = Global.DEFAULT_SORT_BY,
                                                                     direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(borrowService.getBookCanBorrow(bookCriteria, pageable));
    }

    @PostMapping("/book/borrow")
    public ResponseEntity<BorrowResponse> createLoan(@Valid @RequestBody
                                                             BorrowRequest borrowRequest) {
        return ResponseEntity.ok(borrowService.borrowBook(borrowRequest));
    }

    @PutMapping("/book/borrow/loan/{id}")
    public ResponseEntity<SuccessResponseDTO> confirmBorrowBook(@NotNull(message = "Loan id can be not null")
                                                                    @PathVariable long id) {
        borrowService.confirmBorrowBook(id);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Borrow book successful!"));
    }

    @DeleteMapping("/book/borrow/loan/{id}")
    public ResponseEntity<SuccessResponseDTO> cancelBorrowBook(@NotNull(message = "Loan id can be not null")
                                                                @PathVariable long id) {
        borrowService.cancelBorrowBook(id);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Cancel borrow book successful!"));
    }
}
