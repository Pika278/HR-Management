package com.example.hrm.repository.criteria;

import com.example.hrm.entity.Attendance;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor

public class AttendanceCriteria {
    private final EntityManager em;

    public Attendance getAttendanceById(LocalDate localDate, Long userId) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Attendance> criteriaQuery = criteriaBuilder.createQuery(Attendance.class);
        Root<Attendance> root = criteriaQuery.from(Attendance.class);
        criteriaQuery.select(root)
                .where(
                        criteriaBuilder.and(
                                criteriaBuilder.equal(root.get("user").get("Id"), userId),
                                criteriaBuilder.equal(root.get("date"), localDate)
                        )
                );
        TypedQuery<Attendance> query = em.createQuery(criteriaQuery);
        List<Attendance> list = query.getResultList();
        if (list.isEmpty()) return null;
        else if (list.size() == 1) {
            return list.get(0);
        }
        throw new NonUniqueResultException();
    }

    public List<Attendance> getAttendanceByWeek(LocalDate localDate, Long userId) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Attendance> criteriaQuery = criteriaBuilder.createQuery(Attendance.class);
        Root<Attendance> root = criteriaQuery.from(Attendance.class);
        Predicate userIdPredicate = criteriaBuilder.equal(root.get("user").get("Id"), userId);
        Predicate weekPredicate = criteriaBuilder.equal(criteriaBuilder.function("yearweek", Integer.class, root.get("date"), criteriaBuilder.literal(1)),
                criteriaBuilder.function("yearweek", Integer.class, criteriaBuilder.currentDate(), criteriaBuilder.literal(1)));

        criteriaQuery.where(criteriaBuilder.and(userIdPredicate, weekPredicate));

        return em.createQuery(criteriaQuery).getResultList();
    }

    public Page<Attendance> getAttendanceByCurrentMonth(Pageable pageable, Long userId) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Attendance> criteriaQuery = criteriaBuilder.createQuery(Attendance.class);
        Root<Attendance> root = criteriaQuery.from(Attendance.class);

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Months are 0-based in Calendar
        int currentYear = calendar.get(Calendar.YEAR);

        Predicate userIdPredicate = criteriaBuilder.equal(root.get("user").get("Id"), userId);
        Predicate monthPredicate = criteriaBuilder.equal(criteriaBuilder.function("month", Integer.class, root.get("date")), currentMonth);
        Predicate yearPredicate = criteriaBuilder.equal(criteriaBuilder.function("year", Integer.class, root.get("date")), currentYear);

        criteriaQuery.where(criteriaBuilder.and(userIdPredicate, monthPredicate, yearPredicate));

        // Apply sorting
        if (pageable.getSort().isSorted()) {
            List<Order> orders = pageable.getSort().stream()
                    .map(order -> !order.isAscending() ? criteriaBuilder.desc(root.get(order.getProperty())) : criteriaBuilder.asc(root.get(order.getProperty())))
                    .collect(Collectors.toList());
            orders.add(criteriaBuilder.asc(root.get("date"))); // Always sort by date in desc order
            criteriaQuery.orderBy(orders);
        } else {
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get("date"))); // Default sorting by date if no other sorting is provided
        }

        TypedQuery<Attendance> typedQuery = em.createQuery(criteriaQuery);
        int totalRows = typedQuery.getResultList().size();

        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Attendance> resultList = typedQuery.getResultList();

        return new PageImpl<>(resultList, pageable, totalRows);
    }

    public Page<Attendance> getAttendanceByMonth(Pageable pageable, int monthValue, int yearValue, Long userId) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Attendance> criteriaQuery = criteriaBuilder.createQuery(Attendance.class);
        Root<Attendance> root = criteriaQuery.from(Attendance.class);

        Predicate userIdPredicate = criteriaBuilder.equal(root.get("user").get("Id"), userId);
        Predicate monthPredicate = criteriaBuilder.equal(criteriaBuilder.function("month", Integer.class, root.get("date")), monthValue);
        Predicate yearPredicate = criteriaBuilder.equal(criteriaBuilder.function("year", Integer.class, root.get("date")), yearValue);

        criteriaQuery.where(criteriaBuilder.and(userIdPredicate, monthPredicate, yearPredicate));

        // Apply sorting
        if (pageable.getSort().isSorted()) {
            List<Order> orders = pageable.getSort().stream()
                    .map(order -> !order.isAscending() ? criteriaBuilder.desc(root.get(order.getProperty())) : criteriaBuilder.asc(root.get(order.getProperty())))
                    .collect(Collectors.toList());
            orders.add(criteriaBuilder.asc(root.get("date"))); // Always sort by date in desc order
            criteriaQuery.orderBy(orders);
        } else {
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get("date"))); // Default sorting by date if no other sorting is provided
        }

        TypedQuery<Attendance> typedQuery = em.createQuery(criteriaQuery);
        int totalRows = typedQuery.getResultList().size(); // Get total number of rows

        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<Attendance> resultList = typedQuery.getResultList();
        return new PageImpl<>(resultList, pageable, totalRows);
    }
}
