package com.honeychain.notification.event;

import com.honeychain.notification.entity.NotificationType;

public class NotificationEvent {

    private final Long userId;
    private final String title;
    private final String message;
    private final NotificationType type;
    private final String relatedEntityType;
    private final String relatedEntityId;
    private final boolean sendSms;

    public NotificationEvent(Long userId, String title, String message, NotificationType type,
            String relatedEntityType, String relatedEntityId, boolean sendSms) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.relatedEntityType = relatedEntityType;
        this.relatedEntityId = relatedEntityId;
        this.sendSms = sendSms;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public NotificationType getType() {
        return type;
    }

    public String getRelatedEntityType() {
        return relatedEntityType;
    }

    public String getRelatedEntityId() {
        return relatedEntityId;
    }

    public boolean isSendSms() {
        return sendSms;
    }
}
