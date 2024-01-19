package com.bt1.qltv1.controller.user;

import com.bt1.qltv1.dto.ListOutputResult;
import com.bt1.qltv1.dto.SuccessResponseDTO;
import com.bt1.qltv1.dto.returnred.ReturnRequest;
import com.bt1.qltv1.service.ReturnService;
import com.bt1.qltv1.util.Global;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class ReturnController {
    private final ReturnService returnService;

    @GetMapping("/return/books/borrowing")
    public ResponseEntity<ListOutputResult> getAllBookUserBorrowing(@PageableDefault(sort = Global.DEFAULT_SORT_BY,
                                                                              direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(returnService.getBorrowedBook(pageable));
    }

    @PutMapping("/return/books")
    public ResponseEntity<SuccessResponseDTO> returnBook(@Valid @RequestBody ReturnRequest returnRequest){
        returnService.returnBook(returnRequest);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Return book successful!"));
    }
}
