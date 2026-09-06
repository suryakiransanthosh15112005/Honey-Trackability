package com.honeychain.notification.event;

import com.honeychain.notification.entity.Notification;
import com.honeychain.notification.repository.NotificationRepository;
import com.honeychain.notification.service.SmsService;
import com.honeychain.user.entity.User;
import com.honeychain.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NotificationEventListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificationEventListener.class);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SmsService smsService;

    public NotificationEventListener(NotificationRepository notificationRepository,
            UserRepository userRepository,
            SmsService smsService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.smsService = smsService;
    }

    @EventListener
    @Transactional
    public void handleNotificationEvent(NotificationEvent event) {
        User user = userRepository.findById(event.getUserId()).orElse(null);
        if (user == null) {
            logger.warn("NotificationEvent dropped: User ID {} not found", event.getUserId());
            return;
        }

        Notification notification = new Notification(
                user,
                event.getTitle(),
                event.getMessage(),
                event.getType(),
                event.getRelatedEntityType(),
                event.getRelatedEntityId());

        Notification saved = notificationRepository.save(notification);
        logger.info("Created notification #{} ({}) for user #{}", saved.getId(), saved.getType(), user.getId());

        // Dispatch SMS if event is flagged for SMS and user has valid phone
        if (event.isSendSms() && user.getPhoneNumber() != null) {
            String smsText = String.format("%s: %s", event.getTitle(), event.getMessage());
            smsService.sendSms(user.getPhoneNumber(), smsText);
        }
    }
}
