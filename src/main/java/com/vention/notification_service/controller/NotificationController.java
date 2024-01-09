package com.vention.notification_service.controller;

import com.vention.notification_service.domain.NotificationEntity;
import com.vention.notification_service.dto.NotificationResponseDTO;
import com.vention.notification_service.service.MessageProcessorService;
import com.vention.notification_service.service.NotificationRetrieveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationRetrieveService notificationService;
    private final MessageProcessorService messageProcessorService;

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getUnprocessedNotifications(){
        return ResponseEntity.ok(notificationService.getNotSentNotificationsDTO());
    }

    @PostMapping
    public ResponseEntity<Void> sendNotification(@RequestParam Long id){
        NotificationEntity notification = notificationService.getById(id);
        messageProcessorService.process(notification);
        return ResponseEntity.ok().build();
    }
}
