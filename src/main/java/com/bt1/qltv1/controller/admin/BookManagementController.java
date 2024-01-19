package com.bt1.qltv1.controller.admin;

import com.bt1.qltv1.criteria.BaseCriteria;
import com.bt1.qltv1.criteria.BookCriteria;
import com.bt1.qltv1.dto.ListOutputResult;
import com.bt1.qltv1.dto.SuccessResponseDTO;
import com.bt1.qltv1.dto.book.BookRequest;
import com.bt1.qltv1.dto.book.BookResponse;
import com.bt1.qltv1.dto.book.UploadImageResponse;
import com.bt1.qltv1.service.BookService;
import com.bt1.qltv1.util.Global;
import com.bt1.qltv1.validation.CsvType;
import com.bt1.qltv1.validation.ValidImage;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.ISBN;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/admin/book-management")
@RequiredArgsConstructor
@Validated
public class BookManagementController {
    private final BookService bookService;

    @PostMapping("/books")
    public ResponseEntity<SuccessResponseDTO> createNewBook(@ISBN @RequestParam String isbn,
                                                            @NotNull(message = "{book.title.null}")
                                                            @RequestParam String title,
                                                            @ValidImage @RequestParam MultipartFile image,
                                                            @RequestParam(required = false) String description,
                                                            @Min(value = 1, message = "{book.quantity.minimum}")
                                                                @RequestParam Integer quantity,
                                                            @NotNull(message = "{book.price.null}")
                                                                @RequestParam BigDecimal price,
                                                            @NotNull(message = "{book.loan-fee.null}")
                                                            @RequestParam BigDecimal loanFee,
                                                            @NotNull(message = "{book.author.null}")
                                                                @RequestParam long authorId,
                                                            @NotNull(message = "{book.genre.null}")
                                                                @RequestParam long genreId) {
         BookRequest request = BookRequest.builder()
                .isbn(isbn)
                .title(title)
                .description(description)
                .quantity(quantity)
                .price(price)
                .loanFee(loanFee)
                .genreId(genreId)
                .authorId(authorId).build();
        bookService.save(request, image);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.CREATED,
                "Add new book successful!"));
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<BookResponse> updateBook(@NotNull(message = "Id can be not null")
                                                       @PathVariable long id,
                                                   @ISBN @RequestParam String isbn,
                                                   @NotNull(message = "{book.title.null}")
                                                       @RequestParam String title,
                                                   @ValidImage @RequestParam MultipartFile image,
                                                   @RequestParam(required = false) String description,
                                                   @Min(value = 1, message = "{book.quantity.minimum}")
                                                       @RequestParam Integer quantity,
                                                   @RequestParam Boolean available,
                                                   @NotNull(message = "{book.price.null}")
                                                       @RequestParam BigDecimal price,
                                                   @NotNull(message = "{book.loan-fee.null}")
                                                       @RequestParam BigDecimal loanFee,
                                                   @NotNull(message = "{book.author.null}")
                                                       @RequestParam long authorId,
                                                   @NotNull(message = "{book.genre.null}")
                                                       @RequestParam long genreId ) {
        BookRequest request = BookRequest.builder()
                .id(id)
                .isbn(isbn)
                .title(title)
                .description(description)
                .available(available)
                .quantity(quantity)
                .price(price)
                .loanFee(loanFee)
                .genreId(genreId)
                .authorId(authorId).build();
        return ResponseEntity.ok(bookService.save(request,image));
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
                                                           @ValidImage @RequestParam MultipartFile image) {
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
    public ResponseEntity<SuccessResponseDTO> insertNewBookByFile(@CsvType @RequestParam MultipartFile file) {
        bookService.importBookByCsv(file);
        return ResponseEntity.ok(new SuccessResponseDTO(
                HttpStatus.CREATED,
                "Import book in file " + file.getOriginalFilename() + " successful"));
    }
}
