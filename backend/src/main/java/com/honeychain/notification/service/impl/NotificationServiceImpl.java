package com.honeychain.notification.service.impl;

import com.honeychain.common.dto.PageResponse;
import com.honeychain.common.exception.ResourceNotFoundException;
import com.honeychain.notification.dto.NotificationReadResponse;
import com.honeychain.notification.dto.NotificationResponse;
import com.honeychain.notification.entity.Notification;
import com.honeychain.notification.entity.NotificationType;
import com.honeychain.notification.mapper.NotificationMapper;
import com.honeychain.notification.repository.NotificationRepository;
import com.honeychain.notification.service.NotificationService;
import com.honeychain.notification.service.SmsService;
import com.honeychain.user.entity.User;
import com.honeychain.user.repository.UserRepository;
import com.honeychain.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final SmsService smsService;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
            UserService userService,
            UserRepository userRepository,
            NotificationMapper notificationMapper,
            SmsService smsService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.notificationMapper = notificationMapper;
        this.smsService = smsService;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getMyNotifications(String phoneNumber, int page, int size) {
        User user = userService.findEntityByPhoneNumber(phoneNumber);
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        Page<Notification> notificationPage = notificationRepository
                .findAllByUserIdOrderByCreatedAtDesc(user.getId(), pageable);

        List<NotificationResponse> content = notificationPage.getContent().stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                notificationPage.getNumber(),
                notificationPage.getSize(),
                notificationPage.getTotalElements(),
                notificationPage.getTotalPages(),
                notificationPage.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getUnreadCount(String phoneNumber) {
        User user = userService.findEntityByPhoneNumber(phoneNumber);
        long unreadCount = notificationRepository.countByUserIdAndIsReadFalse(user.getId());
        return Map.of("unreadCount", unreadCount);
    }

    @Override
    @Transactional
    public NotificationReadResponse markAsRead(String phoneNumber, Long id) {
        User user = userService.findEntityByPhoneNumber(phoneNumber);
        Notification notification = notificationRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));

        if (!notification.getIsRead()) {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }

        return new NotificationReadResponse(notification.getId(), true, notification.getReadAt());
    }

    @Override
    @Transactional
    public Map<String, Integer> markAllAsRead(String phoneNumber) {
        User user = userService.findEntityByPhoneNumber(phoneNumber);
        List<Notification> unreadList = notificationRepository.findAllByUserIdAndIsReadFalse(user.getId());

        LocalDateTime now = LocalDateTime.now();
        for (Notification notification : unreadList) {
            notification.setIsRead(true);
            notification.setReadAt(now);
        }
        notificationRepository.saveAll(unreadList);

        return Map.of("markedReadCount", unreadList.size());
    }

    @Override
    @Transactional
    public void createNotification(Long userId, String title, String message, NotificationType type,
            String relatedEntityType, String relatedEntityId, boolean sendSms) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            logger.warn("Cannot create notification: User ID {} not found", userId);
            return;
        }

        Notification notification = new Notification(user, title, message, type, relatedEntityType, relatedEntityId);
        Notification saved = notificationRepository.save(notification);
        logger.info("Created notification #{} ({}) for user #{}", saved.getId(), type, userId);

        if (sendSms && user.getPhoneNumber() != null) {
            smsService.sendSms(user.getPhoneNumber(), title + ": " + message);
        }
    }
}
