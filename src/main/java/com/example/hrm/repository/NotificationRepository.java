package com.example.hrm.repository;

import com.example.hrm.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {
    @Query("select n from Notification n order by n.id desc")
    List<Notification> findAllOrderByDesc();
}
