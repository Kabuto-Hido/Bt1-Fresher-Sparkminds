package com.bt1.qltv1.service.criteria;

import com.bt1.qltv1.criteria.BookCriteria;
import com.bt1.qltv1.entity.Book;
import com.bt1.qltv1.entity.Book_;
import com.bt1.qltv1.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

@Service
@Log4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookQueryService extends QueryService<Book> {
    private final BookRepository bookRepository;

    public Page<Book> findByCriteria(BookCriteria criteria, Pageable page){
        log.debug("find by criteria : {}", criteria);
        final Specification<Book> specification = createSpecification(criteria);
        return bookRepository.findAll(specification,page);
    }

    public long countByCriteria(BookCriteria criteria){
        final Specification<Book> specification = createSpecification(criteria);
        return bookRepository.count(specification);
    }

    protected Specification<Book> createSpecification(BookCriteria criteria) {
        Specification<Book> specification = Specification.where(null);
        if(criteria != null){
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Book_.id));
            }
            if (criteria.getIsbn() != null){
                specification = specification.and(buildSpecification(criteria.getIsbn(), Book_.isbn));
            }
            if (criteria.getTitle() != null){
                specification = specification.and(buildSpecification(criteria.getTitle(), Book_.title));
            }
            if (criteria.getDescription() != null){
                specification = specification.and(buildSpecification(criteria.getDescription(),
                        Book_.description));
            }
            if (criteria.getQuantity() != null){
                specification = specification.and(buildRangeSpecification(criteria.getQuantity(), Book_.quantity));
            }
            if (criteria.getAvailable() != null) {
                specification = specification.and(buildSpecification(criteria.getAvailable(), Book_.available));
            }
            if (criteria.getPrice() != null){
                specification = specification.and(buildRangeSpecification(criteria.getPrice(), Book_.price));
            }
            if (criteria.getLoanFee() != null){
                specification = specification.and(buildRangeSpecification(criteria.getLoanFee(), Book_.loanFee));
            }
        }

        return specification;
    }

}
