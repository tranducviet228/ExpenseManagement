package com.kma.project.expensemanagement.service;

public interface NotificationService {

    void sendNotification(String deviceToken, String title, String message);
}
