package com.example.hrm.repository;

import com.example.hrm.entity.Notification;
import com.example.hrm.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("select n from Notification n order by n.id desc")
    List<Notification> findAllOrderByDesc();

    @Query("select n from Notification n where n.title like %?1% order by n.id desc")
    Page<Notification> findAllOrderByDescPaging(Pageable pageable, String keyword);
}
