package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.criteria.BookCriteria;
import com.bt1.qltv1.dto.ListOutputResult;
import com.bt1.qltv1.dto.book.BookResponse;
import com.bt1.qltv1.dto.borrow.BookBorrow;
import com.bt1.qltv1.dto.borrow.BorrowRequest;
import com.bt1.qltv1.dto.borrow.BorrowResponse;
import com.bt1.qltv1.dto.borrow.email.EmailBook;
import com.bt1.qltv1.dto.borrow.email.EmailBorrow;
import com.bt1.qltv1.entity.Book;
import com.bt1.qltv1.entity.Loan;
import com.bt1.qltv1.entity.LoanDetail;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.enumeration.LoanStatus;
import com.bt1.qltv1.exception.BadRequest;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.mapper.BookMapper;
import com.bt1.qltv1.mapper.LoanMapper;
import com.bt1.qltv1.repository.BookRepository;
import com.bt1.qltv1.repository.LoanDetailRepository;
import com.bt1.qltv1.repository.LoanRepository;
import com.bt1.qltv1.repository.UserRepository;
import com.bt1.qltv1.service.BorrowService;
import com.bt1.qltv1.service.EmailService;
import com.bt1.qltv1.service.criteria.BookQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Log4j
public class BorrowServiceImpl implements BorrowService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final LoanDetailRepository loanDetailRepository;
    private final EmailService emailService;
    private final SpringTemplateEngine templateEngine;
    private final BookQueryService bookQueryService;

    private User getCurrentUser() {
        String email = UserDetailsServiceImpl.getEmailLoggedIn();
        return userRepository.findByEmail(email).orElseThrow(() ->
                new NotFoundException("Not found user with email " + email,
                        "get-book-borrow.email.not-exist"));
    }

    private List<Long> getIdBookInLoanDetail(long userId) {
        //get loan detail that user doesn't return book
        List<LoanDetail> loanDetails =
                loanDetailRepository.findByUserIdAndStatus(userId,
                        LoanStatus.BORROW);

        //get genre from book in loan detail
        List<Long> idBookInLoanDetailSet = new ArrayList<>();
        if (loanDetails.isEmpty()) {
            idBookInLoanDetailSet.add(0L);
            return idBookInLoanDetailSet;
        }

        idBookInLoanDetailSet = loanDetails.stream().map(loanDetail ->
                loanDetail.getBook().getId()
        ).toList();
        return idBookInLoanDetailSet;
    }

    @Override
    public ListOutputResult getBookCanBorrow(BookCriteria bookCriteria, Pageable pageable) {
        User user = getCurrentUser();
        //get id book in loan have status BORROW
        List<Long> bookIds = getIdBookInLoanDetail(user.getId());

        //get all book that ignore book in loan has status BORROW
        Page<Book> bookBorrowPage = bookQueryService.findBookCanBorrowByCriteria(bookCriteria, bookIds,
                pageable);

        return resultPaging(bookBorrowPage);
    }

    public ListOutputResult resultPaging(Page<Book> books) {
        if (books.isEmpty()) {
            throw new NotFoundException("No result", "search.borrow-book.notfound");
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

    public void checkBookRequest(BorrowRequest request, long userId) {
        List<BookBorrow> bookBorrows = request.getBookBorrowList();
        List<Long> idGenreRequest = new ArrayList<>();
        Set<Long> bookIdInLoanDetail = new HashSet<>(getIdBookInLoanDetail(userId));

        for (BookBorrow bb : bookBorrows) {
            Book book = bookRepository.findById(bb.getId()).orElseThrow(() ->
                    new NotFoundException("Not found book with id " + bb.getId(),
                            "borrow-book.id-book.not-exist"));

            if (bookIdInLoanDetail.contains(bb.getId())) {
                throw new BadRequest("You have already borrowed this book id " + bb.getId()
                        , "borrow-book.id-book.invalid");
            }

            if (!book.isInStock()) {
                throw new BadRequest("The book is out stock", "borrow-book.status.out-stock");
            }

            //if quantity of book borrow larger than quantity of book in stock
            //throw exception
            if (bb.getQuantity() > book.getQuantity()) {
                throw new BadRequest(String.format("The max quantity of book id %s is %s", book.getId(), book.getQuantity()),
                        "borrow-book.book-quantity.invalid");
            }

            idGenreRequest.add(book.getGenreId().getId());
        }

        Set<Long> idGenreRequestSet = new HashSet<>(idGenreRequest);
        if (idGenreRequestSet.size() < idGenreRequest.size()) {
            throw new BadRequest("You can only borrow 1 book of each genre",
                    "borrow-book.genre.duplicate");
        }
    }

    @Transactional
    @Override
    public BorrowResponse borrowBook(BorrowRequest borrowRequest) {
        User currentUser = getCurrentUser();

        //check genre and quantity
        checkBookRequest(borrowRequest, currentUser.getId());

        LocalDateTime returnDate;
        MathContext m = new MathContext(3);
        try {
            returnDate = LocalDateTime.parse(borrowRequest.getReturnDate(),
                    DateTimeFormatter.ofPattern("ddMMyyyy HHmmss"));

        } catch (DateTimeParseException ex) {
            throw new BadRequest("Please enter right format of date ddMMyyyy HHmmss",
                    "borrow-book.return-date.invalid");
        }

        if (!isValidReturnDate(returnDate)) {
            throw new BadRequest("Please update the return date",
                    "book-borrow.return-date.expired");
        }

        Loan loan = Loan.builder().returnDate(returnDate)
                .user(currentUser)
                .build();
        loan = loanRepository.save(loan);

        //create loan details
        List<LoanDetail> loanDetailList = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.valueOf(0.00);
        for (BookBorrow bb : borrowRequest.getBookBorrowList()) {
            Book book = bookRepository.findById(bb.getId()).orElseThrow(() ->
                    new NotFoundException("Notfound book with id " + bb.getId(),
                            "book.id.invalid"));

            totalPrice = book.getLoanFee().add(totalPrice, m);

            BigDecimal unitPrice = book.getLoanFee().multiply(BigDecimal.valueOf(bb.getQuantity()), m);
            LoanDetail loanDetail = LoanDetail.builder()
                    .unitPrice(unitPrice)
                    .quantity(bb.getQuantity())
                    .loan(loan)
                    .book(book).build();

            loanDetailList.add(loanDetail);
        }
        //update totalPrice of loan
        loanRepository.updateTotalPrice(totalPrice, loan.getId());
        //save all loan detail to db
        loanDetailList = loanDetailRepository.saveAll(loanDetailList);

        loan.setListLoanDetail(loanDetailList);
        loan.setTotalPrice(totalPrice);
        return LoanMapper.toBorrowResponse(loan);
    }

    private boolean isValidReturnDate(LocalDateTime returnDate) {
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(returnDate);
    }

    @Transactional
    @Override
    public void confirmBorrowBook(long loanId) {
        User user = getCurrentUser();
        //check loan is belong is user and have status PENDING_BORROW
        Loan confirmLoan = loanRepository.findByIdAndUserIdAndStatus(loanId, user.getId(),
                LoanStatus.PENDING_BORROW).orElseThrow(() ->
                new NotFoundException("Not found loan with id " + loanId,
                        "loan-confirm.id.not-exist"));

        //check return date
        if (!isValidReturnDate(confirmLoan.getReturnDate())) {
            throw new BadRequest("Please update the return date",
                    "confirm-borrow.return-date.expired");
        }

        List<LoanDetail> loanDetailList = loanDetailRepository.findAllByLoanId(loanId)
                .orElseThrow(() -> new NotFoundException("Not found loan detail", "loan-detail.loan-id.not-found"));

        Set<Long> bookIdInLoanDetail = new HashSet<>(getIdBookInLoanDetail(user.getId()));

        String email = confirmLoan.getUser().getEmail();
        List<Book> updateBooks = new ArrayList<>();
        List<EmailBook> emailBooks = new ArrayList<>();
        int newQuantity;
        for (LoanDetail ld : loanDetailList) {
            Book book = bookRepository.findById(ld.getBook().getId()).orElseThrow(() ->
                    new NotFoundException("Not found book", "book.id.invalid"));

            if (bookIdInLoanDetail.contains(book.getId())) {
                throw new BadRequest("You have already borrowed this book id " + book.getId()
                        , "confirm-book.id-book.invalid");
            }

            //check book is out stock
            if (!book.isInStock()) {
                throw new BadRequest("The book is out stock", "borrow-book.status.out-stock");
            }

            newQuantity = book.getQuantity() - ld.getQuantity();
            //check quantity book
            if (newQuantity < 0) {
                throw new BadRequest(String.
                        format("There are only %s books %s left in stock", book.getQuantity(), book.getTitle())
                        , "confirm-borrow.quantity.out-stock");
            }

            //if quantity = 0 make change status is not enable
            if (newQuantity == 0) {
                book.setInStock(false);
            }
            book.setQuantity(newQuantity);
            updateBooks.add(book);

            EmailBook eb = EmailBook.builder()
                    .fee("$" + book.getLoanFee())
                    .name(book.getTitle())
                    .quantity(ld.getQuantity())
                    .imageName(book.getImage()).build();
            emailBooks.add(eb);
        }
        bookRepository.saveAll(updateBooks);

        //prepare data
        EmailBorrow emailBorrow = EmailBorrow.builder()
                .loanId(loanId)
                .totalPrice("$" + confirmLoan.getTotalPrice())
                .createDate(String.valueOf(confirmLoan.getCreatedDate()))
                .customerEmail(email)
                .customerName(confirmLoan.getUser().getFullName())
                .customerPhone(confirmLoan.getUser().getPhone())
                .emailBooks(emailBooks)
                .build();

        //send mail to user
        sendMailBorrowBook(email, emailBorrow);

        confirmLoan.setPaid(true);
        confirmLoan.setStatus(LoanStatus.BORROW);
        loanRepository.save(confirmLoan);
    }

    @Override
    public void cancelBorrowBook(long loanId) {
        User user = getCurrentUser();
        //check loan is belong is user and have status PENDING_BORROW
        Loan confirmLoan = loanRepository.findByIdAndUserIdAndStatus(loanId, user.getId(),
                LoanStatus.PENDING_BORROW).orElseThrow(() ->
                new NotFoundException("Not found loan with id " + loanId,
                        "loan-confirm.id.not-exist"));

        confirmLoan.setStatus(LoanStatus.CANCELED);
        loanRepository.save(confirmLoan);
    }

    public void sendMailBorrowBook(String mailTo, EmailBorrow emailBorrow) {
        Context context = new Context();
        context.setVariable("loanId", emailBorrow.getLoanId());
        context.setVariable("createDate", emailBorrow.getCreateDate());
        context.setVariable("totalPrice", emailBorrow.getTotalPrice());
        context.setVariable("customerName", emailBorrow.getCustomerName());
        context.setVariable("customerPhone", emailBorrow.getCustomerPhone());
        context.setVariable("customerEmail", emailBorrow.getCustomerEmail());
        context.setVariable("books", emailBorrow.getEmailBooks());

        List<String> imgNames = new ArrayList<>();
        for (EmailBook emailBook : emailBorrow.getEmailBooks()) {
            imgNames.add(emailBook.getImageName());
        }

        String body = templateEngine.process("borrow-book-template.html", context);
        emailService.sendMailWithInline(mailTo,
                body, "Your borrow book", imgNames);
    }
}
