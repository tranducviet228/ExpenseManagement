package com.kma.project.expensemanagement.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PushNotificationRequest {

    private String title;

    private String message;

    private String topic;

    private String token;

    public PushNotificationRequest(String title, String message, String topic) {
    }
}
