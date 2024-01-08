package com.vention.notification_service.dto;

import com.vention.notification_service.domain.enums.NotificationType;
import lombok.Data;

@Data
public class GeneralDTO<T> {

    private T body;

    private NotificationType type;
}
