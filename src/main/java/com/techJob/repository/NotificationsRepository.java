package com.techJob.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.techJob.domain.entity.Notifications;
import com.techJob.domain.entity.User;

public interface NotificationsRepository extends CrudRepository<Notifications, Long> {

    List<Notifications> findByUserOrderByCreatedAtDesc(User user);

    Optional<Notifications> findByIdAndUser(Long id, User user);

    @Modifying
    @Transactional
    @Query("UPDATE Notifications n SET n.isRead = true WHERE n.user = :user AND n.isRead = false")
    void markAllAsReadByUser(User user);
    
    @Modifying
    @Query("DELETE from Notifications n WHERE n.user = :user")
    void deleteByUser(User user);

}