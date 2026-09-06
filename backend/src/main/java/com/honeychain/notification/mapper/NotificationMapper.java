package com.honeychain.notification.mapper;

import com.honeychain.notification.dto.NotificationResponse;
import com.honeychain.notification.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        if (notification == null)
            return null;
        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.getIsRead(),
                notification.getCreatedAt(),
                notification.getReadAt(),
                notification.getRelatedEntityType(),
                notification.getRelatedEntityId());
    }
}
