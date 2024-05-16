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

        Long totalRows = caculateCountByKeyWord(keyword);

        return new PageImpl<User>(query.getResultList(), pageable, totalRows);
    }

    public Page<User> findUserActiveByKeyword(Pageable pageable, String keyword) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> userRoot = criteriaQuery.from(User.class);
        criteriaQuery.select(userRoot)
                .where(
                        criteriaBuilder.and(
                                criteriaBuilder.or(
                                        criteriaBuilder.like(userRoot.get("fullName"),"%" + keyword + "%"),
                                        criteriaBuilder.like(userRoot.get("citizenId"),"%" + keyword + "%"),
                                        criteriaBuilder.like(userRoot.get("jobTitle"),"%" + keyword + "%")
                                ),
                                criteriaBuilder.equal(userRoot.get("is_active"),true)
                        )


                );

        TypedQuery<User> query = em.createQuery(criteriaQuery);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());
        Long totalRows = caculateCountActiveByKeyWord(keyword);
        return new PageImpl<User>(query.getResultList(), pageable, totalRows);
    }

    public Long caculateCountByKeyWord(String keyword) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = criteriaBuilder.createQuery(Long.class);
        Root<User> userRoot = cq.from(User.class);

        cq.select(criteriaBuilder.count(userRoot))
                .where(
                        criteriaBuilder.or(
                                criteriaBuilder.like(userRoot.get("fullName"),"%" + keyword + "%"),
                                criteriaBuilder.like(userRoot.get("citizenId"),"%" + keyword + "%"),
                                criteriaBuilder.like(userRoot.get("jobTitle"),"%" + keyword + "%")
                        )
                );

        Long totalRows = em.createQuery(cq).getSingleResult();
        return totalRows;
    }

    public Long caculateCountActiveByKeyWord(String keyword) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = criteriaBuilder.createQuery(Long.class);
        Root<User> userRoot = cq.from(User.class);

        cq.select(criteriaBuilder.count(userRoot)).where(
                criteriaBuilder.and(
                        criteriaBuilder.or(
                                criteriaBuilder.like(userRoot.get("fullName"),"%" + keyword + "%"),
                                criteriaBuilder.like(userRoot.get("citizenId"),"%" + keyword + "%"),
                                criteriaBuilder.like(userRoot.get("jobTitle"),"%" + keyword + "%")
                        ),
                        criteriaBuilder.equal(userRoot.get("is_active"),true)
                )
        );

        Long totalRows = em.createQuery(cq).getSingleResult();
        return totalRows;
    }
}
