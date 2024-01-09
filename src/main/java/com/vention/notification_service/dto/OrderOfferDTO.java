package com.vention.notification_service.dto;

import com.vention.general.lib.dto.response.UserResponseDTO;
import lombok.Data;

import java.sql.Date;


@Data
public class OrderOfferDTO {
    private Long id;
    private Long userId;
    private UserResponseDTO userSender;
    private String startLocation;
    private String finalLocation;
    private Date deliveryDate;
    private String description;
    private String trackNumber;
    private String sender;
}
