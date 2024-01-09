package com.vention.notification_service.mappers;

import com.vention.notification_service.domain.NotificationEntity;
import com.vention.notification_service.dto.NotificationResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface NotificationMapper {
    List<NotificationResponseDTO> mapEntitiesToDTOs(List<NotificationEntity> notification);
}
