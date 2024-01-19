package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.dto.ListOutputResult;
import com.bt1.qltv1.dto.returnred.BookReturnRequest;
import com.bt1.qltv1.dto.returnred.BookReturnResponse;
import com.bt1.qltv1.dto.returnred.ReturnRequest;
import com.bt1.qltv1.entity.Book;
import com.bt1.qltv1.entity.Loan;
import com.bt1.qltv1.entity.LoanDetail;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.enumeration.LoanStatus;
import com.bt1.qltv1.exception.BadRequest;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.mapper.LoanDetailMapper;
import com.bt1.qltv1.repository.BookRepository;
import com.bt1.qltv1.repository.LoanDetailRepository;
import com.bt1.qltv1.repository.LoanRepository;
import com.bt1.qltv1.repository.UserRepository;
import com.bt1.qltv1.service.ReturnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Log4j
@RequiredArgsConstructor
public class ReturnServiceImpl implements ReturnService {
    private final UserRepository userRepository;
    private final LoanDetailRepository loanDetailRepository;
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    private User getCurrentUser() {
        String email = UserDetailsServiceImpl.getEmailLoggedIn();
        return userRepository.findByEmail(email).orElseThrow(() ->
                new NotFoundException("Not found user with email " + email,
                        "get-book-borrow.email.not-exist"));
    }

    //get all book that user borrowed
    @Override
    public ListOutputResult getBorrowedBook(Pageable pageable) {
        User user = getCurrentUser();

        Page<LoanDetail> loanDetailPage =
                loanDetailRepository.findByLoanUserIdAndLoanStatus(user.getId(), LoanStatus.BORROW,
                        pageable);
        return resultPaging(loanDetailPage);
    }

    @Transactional
    @Override
    public void returnBook(ReturnRequest returnRequest) {
        User user = getCurrentUser();

        //check loan have status borrow
        Loan loan = loanRepository.findByIdAndUserIdAndStatus(returnRequest.getLoanId(),
                        user.getId(), LoanStatus.BORROW)
                .orElseThrow(() ->
                        new NotFoundException("Not found loan with id "
                                + returnRequest.getLoanId(), "return-book.loan.not-found"));

        List<BookReturnRequest> bookReturnRequests = returnRequest.getBookReturnRequests();
        LocalDateTime actualReturnDate = LocalDateTime.now();

        //quantity loan detail in db
        int expectedQuantity = loan.getListLoanDetail().size();

        List<LoanDetail> updateLoanDetails = new ArrayList<>();
        List<Book> updateBooks = new ArrayList<>();
        for (BookReturnRequest request : bookReturnRequests) {
            LoanDetail loanDetail = loanDetailRepository.findFirstByLoanIdAndBookId(returnRequest.getLoanId(),
                    request.getBookId()).orElseThrow(() ->
                    new NotFoundException(
                            String.format("Not found return book with id %s in loan %s", returnRequest.getLoanId(),
                                    request.getBookId())
                            , "return-book.book-id.invalid")
            );

            //check valid quantity that is borrowed
            if (!loanDetail.getQuantity().equals(request.getQuantity())) {
                throw new BadRequest("Number of books borrowed is " + request.getQuantity(),
                        "return-book.book-quantity.invalid");
            }

            //update actual return date
            loanDetail.setActualReturnDate(actualReturnDate);
            updateLoanDetails.add(loanDetail);

            //update quantity book in stock
            Book book = loanDetail.getBook();
            book.setQuantity(book.getQuantity() + request.getQuantity());
            updateBooks.add(book);
        }

        //check is all book in request is like in Loan Detail table
        if (expectedQuantity != updateLoanDetails.size()) {
            throw new BadRequest("You must return all the book you borrowed",
                    "return-book.book.not-enough");
        }

        //update all loan detail to DB
        loanDetailRepository.saveAll(updateLoanDetails);

        //update all book to DB
        bookRepository.saveAll(updateBooks);

        //update loan status
        loan.setStatus(LoanStatus.RETURN);
        loanRepository.save(loan);
    }

    public ListOutputResult resultPaging(Page<LoanDetail> loanDetails) {
        if (loanDetails.isEmpty()) {
            throw new NotFoundException("No result", "borrowed-book.not-found");
        }

        List<BookReturnResponse> bookReturnResponses = new ArrayList<>();
        for (LoanDetail loanDetail : loanDetails) {
            bookReturnResponses.add(LoanDetailMapper.toBookReturnResponse(loanDetail));
        }

        ListOutputResult result = new ListOutputResult(loanDetails.getTotalElements(),
                loanDetails.getTotalPages(), null, null, bookReturnResponses);

        if (loanDetails.hasNext()) {
            result.setNextPage((long) loanDetails.nextPageable().getPageNumber() + 1);
        }
        if (loanDetails.hasPrevious()) {
            result.setPreviousPage((long) loanDetails.previousPageable().getPageNumber() + 1);
        }

        return result;
    }
}
