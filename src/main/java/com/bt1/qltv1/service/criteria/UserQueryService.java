package com.bt1.qltv1.service.criteria;

import com.bt1.qltv1.criteria.UserCriteria;
import com.bt1.qltv1.entity.Role_;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.entity.User_;
import com.bt1.qltv1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

import javax.persistence.criteria.JoinType;
import java.util.List;

@Service
@Log4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryService extends QueryService<User> {
    private final UserRepository userRepository;

    public List<User> findByCriteria(UserCriteria criteria){
        log.debug("find by criteria : {}", criteria);
        final Specification<User> specification = createSpecification(criteria);
        return userRepository.findAll(specification);
    }

    public Page<User> findByCriteria(UserCriteria criteria, Pageable page){
        log.debug("find by criteria : {}", criteria);
        final Specification<User> specification = createSpecification(criteria);
        return userRepository.findAll(specification,page);
    }

    public long countByCriteria(UserCriteria criteria){
        final Specification<User> specification = createSpecification(criteria);
        return userRepository.count(specification);
    }

    protected Specification<User> createSpecification(UserCriteria criteria) {
        Specification<User> specification = Specification.where(null);

        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), User_.id));
            }
            if (criteria.getFullName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFullName(), User_.fullName));
            }
            if(criteria.getVerifyMail() != null){
                specification = specification.and(buildSpecification(criteria.getVerifyMail(), User_.verifyMail));
            }
            if(criteria.getMfaEnabled() != null){
                specification = specification.and(buildSpecification(criteria.getMfaEnabled(),User_.mfaEnabled));
            }
//            if(criteria.getStatus() != null){
//                specification = specification.and(buildStringSpecification(criteria.getStatus(), User_.status));
//            }
            if (criteria.getRole() != null) {
                specification = specification.and(
                                buildSpecification(criteria.getRole(),
                                        root -> root.join(User_.roleSet, JoinType.LEFT).get(Role_.name))
                        );
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhone(), User_.phone));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), User_.email));
            }
        }
        return specification;
    }
}
