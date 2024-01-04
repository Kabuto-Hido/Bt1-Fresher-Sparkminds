package com.bt1.qltv1.controller.admin;

import com.bt1.qltv1.criteria.BookCriteria;
import com.bt1.qltv1.dto.ListOutputResult;
import com.bt1.qltv1.dto.SuccessResponseDTO;
import com.bt1.qltv1.dto.author.AuthorRequest;
import com.bt1.qltv1.dto.book.BookRequest;
import com.bt1.qltv1.dto.book.BookResponse;
import com.bt1.qltv1.dto.book.UploadImageResponse;
import com.bt1.qltv1.service.BookService;
import com.bt1.qltv1.util.Global;
import com.bt1.qltv1.validation.ValidImage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin/book-management")
@RequiredArgsConstructor
public class BookManagementController {
    private final BookService bookService;
    @PostMapping("/books")
    public ResponseEntity<SuccessResponseDTO> createNewBook(@Valid @RequestBody
                                                              BookRequest request) {
        bookService.save(request);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Add new book successful!"));
    }
    @PutMapping("/books/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable long id,
                                                    @Valid @RequestBody BookRequest request){
        request.setId(id);
        return ResponseEntity.ok(bookService.save(request));
    }
    @DeleteMapping("/books/{id}")
    public ResponseEntity<SuccessResponseDTO> deleteBook(@PathVariable long id){
        bookService.deleteById(id);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Delete book successful!"));
    }

    @PutMapping("books/{id}/image")
    public ResponseEntity<UploadImageResponse> uploadImage(@PathVariable long id,
                                                           @RequestParam MultipartFile image){
        return null;
    }

    @GetMapping("/books")
    public ResponseEntity<ListOutputResult> getAllBook(BookCriteria bookCriteria,
                                                       @RequestParam(required = false,
                                                               defaultValue = Global.DEFAULT_PAGE) String page,
                                                       @RequestParam(required = false,
                                                               defaultValue = Global.DEFAULT_LIMIT) String limit,
                                                       @RequestParam(required = false,
                                                               defaultValue = "desc") String order,
                                                       @RequestParam(required = false,
                                                               defaultValue = Global.DEFAULT_SORT_BY) String sortBy) {
        return ResponseEntity.ok(bookService.findAllBook(bookCriteria, page, limit, order, sortBy));
    }
}
