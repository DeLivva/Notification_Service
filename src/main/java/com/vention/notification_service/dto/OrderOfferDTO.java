package com.vention.notification_service.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class OrderOfferDTO {
    private Long id;
    private Long userId;
    private String senderName;
    private String startLocation;
    private String finalLocation;
    private Timestamp deliveryDate;
    private String description;
    private String trackNumber;
    private String sender;
}
