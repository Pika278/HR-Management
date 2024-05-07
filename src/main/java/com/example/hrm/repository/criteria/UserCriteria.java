package com.example.hrm.repository.criteria;

import com.example.hrm.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class UserCriteria {
    private final EntityManager em;

    public UserCriteria(EntityManager em) {
        this.em = em;
    }

    public Page<User> findByKeyword(Pageable pageable, String keyword) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> userRoot = criteriaQuery.from(User.class);
        criteriaQuery.select(userRoot)
                .where(
                        criteriaBuilder.or(
                                criteriaBuilder.like(userRoot.get("fullName"),"%" + keyword + "%"),
                                criteriaBuilder.like(userRoot.get("citizenId"),"%" + keyword + "%"),
                                criteriaBuilder.like(userRoot.get("jobTitle"),"%" + keyword + "%")
                        )
                );

        TypedQuery<User> query = em.createQuery(criteriaQuery);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        CriteriaQuery<Long> cq = criteriaBuilder.createQuery(Long.class);
        cq.select(criteriaBuilder.count(cq.from(User.class)));
        Long totalRows = em.createQuery(cq).getSingleResult();

        return new PageImpl<User>(query.getResultList(), pageable, totalRows);
    }
}