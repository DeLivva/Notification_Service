package com.vention.notification_service.domain;

import com.vention.notification_service.domain.enums.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "notification")
public class NotificationEntity extends BaseEntity {
    @Enumerated(value = EnumType.STRING)
    private NotificationType type;

    @Column(nullable = false)
    private String title;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "msg_data")
    private String data;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Boolean isSent;
}
