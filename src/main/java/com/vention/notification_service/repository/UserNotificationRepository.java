package com.vention.notification_service.repository;

import com.vention.notification_service.domain.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
    Optional<List<UserNotification>> findAllByUserId(Long userId);
 }
