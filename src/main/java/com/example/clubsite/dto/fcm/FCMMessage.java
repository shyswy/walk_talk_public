package com.example.clubsite.dto.fcm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class FCMMessage {
    private boolean validate_only;
    private Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private String token;
        private String topic;
        private Notification notification;
        private Data data;      // Android
        private Apns apns;   // iOS
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {
        private String title;
        private String body;
        private String image;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Apns {
        private Payload payload;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Data {
        private String linkUrl;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Payload {
        private String notiType;
        private Long chatId;
        private String nickName;
        private String imagePath;
        private String message;
        private String linkUrl;
        private Aps aps;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Aps {
        private Alert alert;
        private String sound;
        private int badge;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Alert {
        private String body;
    }
}
