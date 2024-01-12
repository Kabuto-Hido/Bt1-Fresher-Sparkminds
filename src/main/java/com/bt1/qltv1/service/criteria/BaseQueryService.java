package com.bt1.qltv1.service.criteria;

import com.bt1.qltv1.criteria.BaseCriteria;
import com.bt1.qltv1.entity.BaseEntity;
import com.bt1.qltv1.entity.BaseEntity_;
import lombok.extern.log4j.Log4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

import java.time.LocalDateTime;

@Service
@Log4j
@Transactional(readOnly = true)
public class BaseQueryService<T> extends QueryService<BaseEntity> {
    protected Specification<T> createSpecification(BaseCriteria criteria) {
        Specification<T> specification = Specification.where(null);
        if (criteria.getFromTime() != null) {
            specification = specification.and((root, query, criteriaBuilder)
                    -> criteriaBuilder.greaterThanOrEqualTo(root.get(BaseEntity_.CREATED_DATE)
                            .as(LocalDateTime.class),
                    criteria.getFormatFromTime()));
        }

        if (criteria.getToTime() != null) {
            specification = specification.and((root, query, criteriaBuilder)
                    -> criteriaBuilder.lessThanOrEqualTo(root.get(BaseEntity_.CREATED_DATE)
                            .as(LocalDateTime.class),
                    criteria.getFormatToTime()));
        }
        return specification;
    }

}
