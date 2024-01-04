package com.vention.notification_service.dto;

import lombok.Data;

@Data
public class DisputeClosedNotificationDTO {
    private Long orderId;
    private Long disputeId;
}
