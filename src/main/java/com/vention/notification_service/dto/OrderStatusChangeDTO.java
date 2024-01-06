package com.vention.notification_service.dto;

import lombok.Data;

@Data
public class OrderStatusChangeDTO {
    private String trackNumber;
    private String driverEmail;
    private String status;
}
