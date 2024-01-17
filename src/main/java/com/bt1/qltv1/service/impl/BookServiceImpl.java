package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.criteria.BaseCriteria;
import com.bt1.qltv1.criteria.BookCriteria;
import com.bt1.qltv1.dto.ListOutputResult;
import com.bt1.qltv1.dto.book.BookRequest;
import com.bt1.qltv1.dto.book.BookResponse;
import com.bt1.qltv1.dto.book.UploadImageResponse;
import com.bt1.qltv1.entity.Author;
import com.bt1.qltv1.entity.Book;
import com.bt1.qltv1.entity.Genre;
import com.bt1.qltv1.exception.BadRequest;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.mapper.BookMapper;
import com.bt1.qltv1.repository.AuthorRepository;
import com.bt1.qltv1.repository.BookRepository;
import com.bt1.qltv1.repository.GenreRepository;
import com.bt1.qltv1.service.BookService;
import com.bt1.qltv1.service.FileService;
import com.bt1.qltv1.service.ImageService;
import com.bt1.qltv1.service.criteria.BookQueryService;
import com.bt1.qltv1.util.Global;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Log4j
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookQueryService bookQueryService;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final FileService fileService;
    private final ImageService imageService;

    private Book findBookById(long id) {
        return bookRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Not found book with id " + id,
                        "book.id.not-exist"));
    }

    public ListOutputResult resultPaging(Page<Book> books) {
        if (books.isEmpty()) {
            throw new NotFoundException("No result", "search.book.notfound");
        }

        List<BookResponse> bookResponses = new ArrayList<>();
        for (Book book : books) {
            bookResponses.add(BookMapper.tobBookResponse(book));
        }

        ListOutputResult result = new ListOutputResult(books.getTotalElements(),
                books.getTotalPages(), null, null, bookResponses);

        if (books.hasNext()) {
            result.setNextPage((long) books.nextPageable().getPageNumber() + 1);
        }
        if (books.hasPrevious()) {
            result.setPreviousPage((long) books.previousPageable().getPageNumber() + 1);
        }

        return result;
    }

    @Override
    public ListOutputResult findAllBook(BookCriteria bookCriteria, BaseCriteria baseCriteria,
                                        Pageable pageable) {
        Page<Book> bookPage = bookQueryService.findByCriteria(bookCriteria, baseCriteria, pageable);
        return resultPaging(bookPage);
    }

    @Override
    public BookResponse save(BookRequest bookRequest, MultipartFile image) {
        Author author = authorRepository.findById(bookRequest.getAuthorId()).orElseThrow(() ->
                new NotFoundException("Not found author with id " + bookRequest.getAuthorId(),
                        "author.id.not-exist"));

        Genre genre = genreRepository.findById(bookRequest.getGenreId()).orElseThrow(() ->
                new NotFoundException("Not found genre with id " + bookRequest.getGenreId(),
                        "genre.id.not-exist"));

        //upload image book
        Path filePath = imageService.saveImageToStorage(bookRequest.getIsbn(), Global.BOOK_DIR, image);
        bookRequest.setImage(filePath.getFileName().toString());

        Book book;
        if (bookRequest.getId() != null) {
            book = findBookById(bookRequest.getId());

            if(!book.getIsbn().equals(bookRequest.getIsbn())){
                checkExistIsbn(bookRequest.getIsbn());
                book.setIsbn(bookRequest.getIsbn());
            }
            book.setTitle(bookRequest.getTitle());
            book.setDescription(bookRequest.getDescription());
            book.setQuantity(bookRequest.getQuantity());
            book.setAvailable(bookRequest.isAvailable());
            book.setPrice(bookRequest.getPrice());
            book.setLoanFee(bookRequest.getLoanFee());
            book.setImage(bookRequest.getImage());
        } else {
            checkExistIsbn(bookRequest.getIsbn());
            book = BookMapper.toEntity(bookRequest);
        }

        book.setAuthorId(author);
        book.setGenreId(genre);
        book = bookRepository.save(book);
        return BookMapper.tobBookResponse(book);
    }

    @Override
    public void deleteById(long id) {
        Book bookDeleted = bookRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Not found book with id " + id, "book.delete.id-not-exist"));

        bookDeleted.setAvailable(false);
        bookRepository.save(bookDeleted);
    }

    @Transactional
    @Override
    public void importBookByCsv(MultipartFile file) {
        try {

            List<String[]> records = fileService.readCsvFileWithoutHeader(file, Book.class);

            List<Book> bookImportList = new ArrayList<>();
            for (String[] line : records) {
                //validation data
                validationData(line);

                Author author = findAuthorByName(line[6]);
                Genre genre = findGenreByName(line[7]);

                Book newBook = Book.builder()
                        .isbn(line[0])
                        .title(line[1])
                        .description(line[2])
                        .quantity(Integer.valueOf(line[3]))
                        .price(NumberUtils.createBigDecimal(line[4]))
                        .loanFee(NumberUtils.createBigDecimal(line[5]))
                        .authorId(author)
                        .genreId(genre)
                        .build();
                bookImportList.add(newBook);
            }

            bookRepository.saveAll(bookImportList);
        } catch (IOException | CsvException e) {
            log.error("file error: " + e.getMessage());
            throw new BadRequest(e.getMessage(), "file.import-csv.error");
        } finally {
            log.info("Import user by csv file done!");
        }
    }

    @Override
    public UploadImageResponse uploadBookImage(long id, MultipartFile image) {
        Book book = findBookById(id);

        Path filePath = imageService.saveImageToStorage(book.getIsbn(), Global.BOOK_DIR, image);

        book.setImage(filePath.getFileName().toString());
        bookRepository.save(book);

        return UploadImageResponse.builder().url(filePath.toAbsolutePath().toString()).build();
    }

    @Override
    public void deleteBookImage(long id) {
        Book book = findBookById(id);

        imageService.deleteImage(book.getImage(), Global.BOOK_DIR);

        book.setImage(Global.DEFAULT_IMAGE);
        bookRepository.save(book);
    }

    @Override
    public UploadImageResponse getBookImage(long id) {
        Book book = findBookById(id);
        String url = imageService.getDirImage(book.getImage(), Global.BOOK_DIR);

        return UploadImageResponse.builder()
                .url(url).build();
    }

    private Author findAuthorByName(String authorName) {
        return authorRepository.findDistinctFirstByNameContainingIgnoreCase(authorName)
                .orElseThrow(() -> new NotFoundException("Not found author with name " + authorName,
                        "author.name.not-exist"));
    }

    private Genre findGenreByName(String genreName) {
        return genreRepository.findDistinctFirstByNameContainingIgnoreCase(genreName)
                .orElseThrow(() -> new NotFoundException("Not found genre with name " + genreName,
                        "genre.name.not-exist"));
    }

    private void validationData(String[] data) {
        Optional<Book> bookByIsbn = bookRepository.findByIsbn(data[0]);
        if (bookByIsbn.isPresent()) {
            throw new BadRequest("Isbn code already exist!!",
                    "book.isbn.isbn-existed");
        }

        if (Boolean.FALSE.equals(isNumber(data[3]))) {
            throw new BadRequest("Quantity must be a number!!",
                    "book.quantity.invalid");
        }
        if (Boolean.FALSE.equals(isNumber(data[4]))) {
            throw new BadRequest("Price must be a number!!",
                    "book.price.invalid");
        }

        if (Boolean.FALSE.equals(isNumber(data[5]))) {
            throw new BadRequest("Loan Fee must be a number!!",
                    "book.loan-fee.invalid");
        }
    }

    private Boolean isNumber(String s) {
        return NumberUtils.isCreatable(s);
    }

    private void checkExistIsbn(String isbn){
        Optional<Book> bookByIsbn = bookRepository.findByIsbn(isbn);
        if (bookByIsbn.isPresent()) {
            throw new BadRequest("Isbn code duplicate, Please retype!",
                    "book.isbn.isbn-existed");
        }

    }
}
