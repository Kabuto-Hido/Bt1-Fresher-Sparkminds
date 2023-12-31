package com.bt1.qltv1.service.criteria;

import com.bt1.qltv1.criteria.BookCriteria;
import com.bt1.qltv1.criteria.UserCriteria;
import com.bt1.qltv1.entity.*;
import com.bt1.qltv1.exception.BadRequest;
import com.bt1.qltv1.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

import javax.persistence.criteria.JoinType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@Log4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookQueryService extends QueryService<Book> {
    private final BookRepository bookRepository;

    public Page<Book> findByCriteria(BookCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}", criteria);
        Specification<Book> specification = createSpecification(criteria);
        return bookRepository.findAll(specification, page);
    }

    public long countByCriteria(BookCriteria criteria) {
        final Specification<Book> specification = createSpecification(criteria);
        return bookRepository.count(specification);
    }

    protected Specification<Book> createSpecification(BookCriteria criteria) {
        Specification<Book> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Book_.id));
            }
            if (criteria.getIsbn() != null) {
                specification = specification.and(buildSpecification(criteria.getIsbn(), Book_.isbn));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildSpecification(criteria.getTitle(), Book_.title));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildSpecification(criteria.getDescription(),
                        Book_.description));
            }
            if (criteria.getQuantity() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQuantity(), Book_.quantity));
            }
            if (criteria.getAvailable() != null) {
                specification = specification.and(buildSpecification(criteria.getAvailable(), Book_.available));
            }
            if (criteria.getPrice() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrice(), Book_.price));
            }
            if (criteria.getLoanFee() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLoanFee(), Book_.loanFee));
            }
            if (criteria.getAuthorId() != null) {
                specification = specification.and(
                        buildSpecification(criteria.getAuthorId(),
                                root -> root.join(Book_.authorId, JoinType.LEFT).get(Author_.id))
                );
            }
            if (criteria.getGenreId() != null) {
                specification = specification.and(
                        buildSpecification(criteria.getGenreId(),
                                root -> root.join(Book_.genreId, JoinType.LEFT).get(Genre_.id))
                );
            }
            if(criteria.getFromTime()!= null) {
                try {
                    LocalDateTime from = fromString(criteria.getFromTime());
                    specification = specification.and((root, query, criteriaBuilder)
                            -> criteriaBuilder.greaterThanOrEqualTo(root.get(BaseEntity_.createdDate),
                            from));
                }catch (DateTimeParseException ex){
                   throw new BadRequest("Please enter right format of date ddMMyyyy HHmmss",
                           "get-book.from-date.invalid");
                }
            }

            if(criteria.getToTime()!= null) {
                try {
                    LocalDateTime to = fromString(criteria.getFromTime());
                    specification = specification.and((root, query, criteriaBuilder)
                            -> criteriaBuilder.lessThanOrEqualTo(root.get(BaseEntity_.createdDate),
                            to));
                }catch (DateTimeParseException ex){
                    throw new BadRequest("Please enter right format of date ddMMyyyy HHmmss",
                            "get-book.from-date.invalid");
                }
            }
        }
        return specification;
    }

    private LocalDateTime fromString(String dateTime) throws DateTimeParseException {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("ddMMyyyy HHmmss"));
    }
}
