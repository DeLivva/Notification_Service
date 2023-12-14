package com.vention.notification_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ConfirmationTokenDto {

    @JsonProperty("email")
    private String email;

    @JsonProperty("token")
    private String token;
}
