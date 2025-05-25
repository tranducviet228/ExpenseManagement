package com.kma.project.expensemanagement.service;


import java.util.List;

public interface NotificationService {

    void sendNotification(List<String> deviceTokens, String title, String message);
}
