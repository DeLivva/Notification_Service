package com.vention.notification_service.dto;

import com.vention.notification_service.domain.enums.NotificationType;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class NotificationResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Timestamp createdAt;
    private NotificationType type;
}
