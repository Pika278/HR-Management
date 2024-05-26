package com.example.hrm.repository.criteria;

import com.example.hrm.dto.response.DepartmentResponse;
import com.example.hrm.entity.Department;
import com.example.hrm.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DepartmentCriteria {
    private final EntityManager entityManager;

    public Page<DepartmentResponse> getDepartmentsWithActiveUsers(Pageable pageable, String namePattern) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DepartmentResponse> query = cb.createQuery(DepartmentResponse.class);

        Root<Department> department = query.from(Department.class);
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<User> subRoot = subquery.from(User.class);
        subquery.select(cb.count(subRoot))
                .where(cb.and(
                        cb.equal(subRoot.get("department"), department),
                        cb.isTrue(subRoot.get("is_active"))
                ));

        query.select(cb.construct(DepartmentResponse.class,
                        department.get("id"),
                        department.get("name"),
                        cb.coalesce(subquery.getSelection(), 0L)))
                .where(cb.like(department.get("name"), "%" + namePattern + "%"));
        // Apply sorting
        if (pageable.getSort().isSorted()) {
            List<Order> orders = pageable.getSort().stream()
                    .map(order -> order.isAscending() ? cb.asc(department.get(order.getProperty())) : cb.desc(department.get(order.getProperty())))
                    .collect(Collectors.toList());
            query.orderBy(orders);
        }

        TypedQuery<DepartmentResponse> typedQuery = entityManager.createQuery(query);

        // Apply pagination
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        // Get results
        List<DepartmentResponse> resultList = typedQuery.getResultList();

        // Count total results for pagination
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Department> countRoot = countQuery.from(Department.class);
        // Apply sorting
        if (pageable.getSort().isSorted()) {
            List<Order> orders = pageable.getSort().stream()
                    .map(order -> order.isAscending() ? cb.asc(department.get(order.getProperty())) : cb.desc(department.get(order.getProperty())))
                    .collect(Collectors.toList());
            query.orderBy(orders);
        }
        countQuery.select(cb.countDistinct(countRoot))
                .where(cb.like(countRoot.get("name"), "%" + namePattern + "%"));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(resultList, pageable, total);
    }
}
