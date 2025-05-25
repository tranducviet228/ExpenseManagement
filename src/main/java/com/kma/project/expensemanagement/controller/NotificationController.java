package com.kma.project.expensemanagement.controller;

import com.kma.project.expensemanagement.dto.request.PushNotificationRequest;
import com.kma.project.expensemanagement.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/v1/notifications")
@RestController
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @PostMapping("/send-notification")
    public void sendNotification(@RequestBody PushNotificationRequest request) {
        String deviceToken = request.getToken();
        String title = request.getTitle();
        String message = request.getMessage();

        notificationService.sendNotification(List.of(deviceToken), title, message);
    }

}
