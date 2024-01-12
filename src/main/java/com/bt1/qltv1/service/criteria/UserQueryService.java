package com.bt1.qltv1.service.criteria;

import com.bt1.qltv1.criteria.BaseCriteria;
import com.bt1.qltv1.criteria.UserCriteria;
import com.bt1.qltv1.entity.*;
import com.bt1.qltv1.enumeration.UserStatus;
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
    private final BaseQueryService<User> baseQueryService;

    public List<User> findByCriteria(UserCriteria userCriteria){
        log.debug("find by criteria : {}", userCriteria);
        Specification<User> specification = createSpecification(userCriteria);
        return userRepository.findAll(specification);
    }

    public Page<User> findByCriteria(UserCriteria userCriteria, BaseCriteria baseCriteria, Pageable page){
        log.debug("find by criteria : {}", userCriteria);
        Specification<User> specification = createSpecification(userCriteria)
                .and(baseQueryService.createSpecification(baseCriteria));
        return userRepository.findAll(specification,page);
    }

    public long countByCriteria(UserCriteria userCriteria, BaseCriteria baseCriteria){
        Specification<User> specification = createSpecification(userCriteria)
                .and(baseQueryService.createSpecification(baseCriteria));

        return userRepository.count(specification);
    }

    public Specification<User> setNoStatus(UserStatus status){
        return (root, query, builder) -> builder.notEqual(root.get(Account_.STATUS), status);
    }

    protected Specification<User> createSpecification(UserCriteria criteria) {
        Specification<User> specification = Specification.where(setNoStatus(UserStatus.DELETED));

        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Account_.id));
            }
            if (criteria.getFullName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFullName(), Account_.fullName));
            }
            if(criteria.getVerifyMail() != null){
                specification = specification.and(buildSpecification(criteria.getVerifyMail(), Account_.verifyMail));
            }
            if(criteria.getMfaEnabled() != null){
                specification = specification.and(buildSpecification(criteria.getMfaEnabled(), Account_.mfaEnabled));
            }
            if(criteria.getStatus() != null){
                specification = specification.and(buildSpecification(criteria.getStatus(), Account_.status));
            }
            if (criteria.getRole() != null) {
                specification = specification.and(
                                buildSpecification(criteria.getRole(),
                                        root -> root.join(Account_.roleSet, JoinType.LEFT).get(Role_.name))
                        );
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhone(), User_.phone));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), Account_.email));
            }
        }
        return specification;
    }

}
