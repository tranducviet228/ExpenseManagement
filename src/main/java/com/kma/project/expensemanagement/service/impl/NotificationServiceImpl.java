package com.kma.project.expensemanagement.service.impl;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.kma.project.expensemanagement.entity.DeviceTokenEntity;
import com.kma.project.expensemanagement.repository.DeviceTokenRepository;
import com.kma.project.expensemanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    DeviceTokenRepository deviceTokenRepository;
    @Value("${app.firebase-configuration-file}")
    private String firebaseConfigPath;

    public void sendNotification(List<String> deviceTokens, String title, String message) {

        List<DeviceTokenEntity> deviceTokenEntities = deviceTokenRepository.findAllByTokenIn(deviceTokens);
        List<Message> messages = deviceTokenEntities.stream().map(deviceTokenEntity -> Message
                        .builder()
                        .setToken(deviceTokenEntity.getToken())
                        .setNotification(Notification.builder().setTitle(title).setBody(message).build())
                        .build()).collect(Collectors.toList());
        messages.forEach(mess -> FirebaseMessaging.getInstance().sendAsync(mess));
    }

    @PostConstruct
    private FirebaseMessaging initFirebaseConnection() {
        log.info("Start init");
        try {
            InputStream serviceAccount = new ClassPathResource(firebaseConfigPath).getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Firebase application has been initialized");

        return null;
    }

}
