package com.bt1.qltv1.controller.admin;

import com.bt1.qltv1.criteria.BaseCriteria;
import com.bt1.qltv1.criteria.BookCriteria;
import com.bt1.qltv1.dto.ListOutputResult;
import com.bt1.qltv1.dto.SuccessResponseDTO;
import com.bt1.qltv1.dto.author.AuthorRequest;
import com.bt1.qltv1.dto.book.BookRequest;
import com.bt1.qltv1.dto.book.BookResponse;
import com.bt1.qltv1.dto.book.UploadImageResponse;
import com.bt1.qltv1.entity.Book;
import com.bt1.qltv1.service.BookService;
import com.bt1.qltv1.service.ImageService;
import com.bt1.qltv1.util.Global;
import com.bt1.qltv1.validation.ValidImage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/v1/admin/book-management")
@RequiredArgsConstructor
public class BookManagementController {
    private final BookService bookService;
    private final ImageService imageService;

    @PostMapping("/books")
    public ResponseEntity<SuccessResponseDTO> createNewBook(@Valid @RequestBody
                                                            BookRequest request) {
        bookService.save(request);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Add new book successful!"));
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<BookResponse> updateBook(@NotNull(message = "Id can be not null")
                                                       @PathVariable long id,
                                                   @Valid @RequestBody BookRequest request) {
        request.setId(id);
        return ResponseEntity.ok(bookService.save(request));
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<SuccessResponseDTO> deleteBook(@NotNull(message = "Id can be not null")
                                                             @PathVariable long id) {
        bookService.deleteById(id);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Delete book successful!"));
    }

    @PutMapping("books/{id}/image")
    public ResponseEntity<UploadImageResponse> uploadImage(@NotNull(message = "Id can be not null")
                                                               @PathVariable long id,
                                                           @RequestParam MultipartFile image) {
        return ResponseEntity.ok(bookService.uploadBookImage(id, image));
    }

    @DeleteMapping("/books/{id}/image")
    public ResponseEntity<SuccessResponseDTO> deleteImageBook(@NotNull(message = "Id can be not null")
                                                                  @PathVariable long id) {
        bookService.deleteBookImage(id);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Delete image book successful!"));
    }

    @GetMapping("/books/{id}/image")
    public ResponseEntity<UploadImageResponse> getImage(@NotNull(message = "Id can be not null")
                                                            @PathVariable long id){
        return ResponseEntity.ok(bookService.getBookImage(id));
    }

    @GetMapping("/books")
    public ResponseEntity<ListOutputResult> getAllBook(BookCriteria bookCriteria,
                                                       BaseCriteria baseCriteria,
                                                       @PageableDefault(sort = Global.DEFAULT_SORT_BY,
                                                               direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(bookService.findAllBook(bookCriteria, baseCriteria, pageable));
    }

    @PostMapping("/books/upload-csv")
    public ResponseEntity<SuccessResponseDTO> insertNewBookByFile(@RequestParam MultipartFile file) {
        bookService.importBookByCsv(file);
        return ResponseEntity.ok(new SuccessResponseDTO(
                HttpStatus.OK,
                "Import book in file " + file.getOriginalFilename() + " successful"));
    }
}
