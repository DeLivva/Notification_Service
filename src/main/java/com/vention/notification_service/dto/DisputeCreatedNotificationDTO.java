package com.vention.notification_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class DisputeCreatedNotificationDTO {
    private Long disputeId;
    private Long orderId;
    private String ownerName;
    private String driverName;
    private String description;
    private List<String> adminEmails;
}
