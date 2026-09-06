package com.honeychain.notification.service;

import com.honeychain.common.dto.PageResponse;
import com.honeychain.notification.dto.NotificationReadResponse;
import com.honeychain.notification.dto.NotificationResponse;
import com.honeychain.notification.entity.NotificationType;

import java.util.Map;

public interface NotificationService {

    PageResponse<NotificationResponse> getMyNotifications(String phoneNumber, int page, int size);

    Map<String, Long> getUnreadCount(String phoneNumber);

    NotificationReadResponse markAsRead(String phoneNumber, Long id);

    Map<String, Integer> markAllAsRead(String phoneNumber);

    void createNotification(Long userId, String title, String message, NotificationType type,
            String relatedEntityType, String relatedEntityId, boolean sendSms);
}
