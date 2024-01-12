package com.bt1.qltv1.service.criteria;

import com.bt1.qltv1.criteria.BaseCriteria;
import com.bt1.qltv1.criteria.BookCriteria;
import com.bt1.qltv1.entity.Author_;
import com.bt1.qltv1.entity.Book;
import com.bt1.qltv1.entity.Book_;
import com.bt1.qltv1.entity.Genre_;
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

@Service
@Log4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookQueryService extends QueryService<Book> {
    private final BookRepository bookRepository;
    private final BaseQueryService<Book> baseQueryService;

    public Page<Book> findByCriteria(BookCriteria bookcriteria, BaseCriteria baseCriteria, Pageable page) {
        log.debug("find by criteria : {}", bookcriteria);
        Specification<Book> specification = createSpecification(bookcriteria)
                .and(baseQueryService.createSpecification(baseCriteria));
        return bookRepository.findAll(specification, page);
    }

    public long countByCriteria(BookCriteria bookcriteria, BaseCriteria baseCriteria) {
        final Specification<Book> specification = createSpecification(bookcriteria)
                .and(baseQueryService.createSpecification(baseCriteria));
        return bookRepository.count(specification);
    }

    public Specification<Book> setAvailable(boolean available){
        return (root, query, builder) -> builder.equal(root.get(Book_.AVAILABLE), available);
    }

    protected Specification<Book> createSpecification(BookCriteria criteria) {
        Specification<Book> specification = Specification.where(setAvailable(true));
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
        }
        return specification;
    }

}
