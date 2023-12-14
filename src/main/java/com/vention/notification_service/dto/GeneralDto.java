package com.vention.notification_service.dto;

import com.vention.notification_service.domain.NotificationType;
import lombok.Data;

@Data
public class GeneralDto<T> {

    private T data;

    private NotificationType type;
}
