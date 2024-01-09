package com.vention.notification_service.dto;

import lombok.Data;

import java.util.Date;

@Data
public class OrderOfferDTO {
    private Long id;
    private Long userId;
    private String senderName;
    private String startLocation;
    private String finalLocation;
    private Date deliveryDate;
    private String description;
    private String trackNumber;
    private String sender;
}
