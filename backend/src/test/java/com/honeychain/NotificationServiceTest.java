package com.honeychain;

import com.honeychain.common.dto.PageResponse;
import com.honeychain.notification.dto.NotificationReadResponse;
import com.honeychain.notification.dto.NotificationResponse;
import com.honeychain.notification.entity.NotificationType;
import com.honeychain.notification.service.NotificationService;
import com.honeychain.security.jwt.JwtService;
import com.honeychain.user.entity.Role;
import com.honeychain.user.entity.User;
import com.honeychain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class NotificationServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String USER_A_PHONE = "9988776601";
    private static final String USER_B_PHONE = "9988776602";

    private User userA;
    private User userB;

    @BeforeEach
    void setup() {
        userA = userRepository.findByPhoneNumber(USER_A_PHONE).orElseGet(() -> {
            User u = new User(USER_A_PHONE, passwordEncoder.encode("Test@1234"), Role.BEEKEEPER, true);
            return userRepository.save(u);
        });

        userB = userRepository.findByPhoneNumber(USER_B_PHONE).orElseGet(() -> {
            User u = new User(USER_B_PHONE, passwordEncoder.encode("Test@1234"), Role.CUSTOMER, true);
            return userRepository.save(u);
        });
    }

    @Test
    @DisplayName("Create notification and retrieve via service")
    void createAndGetNotifications() {
        notificationService.createNotification(
                userA.getId(), "Lab Result Available", "Purity 98%", NotificationType.LAB_RESULT, "BATCH", "HC-001",
                false);

        PageResponse<NotificationResponse> result = notificationService.getMyNotifications(USER_A_PHONE, 0, 10);
        assertThat(result.getContent()).hasSizeGreaterThanOrEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Lab Result Available");
        assertThat(result.getContent().get(0).getIsRead()).isFalse();
    }

    @Test
    @DisplayName("Unread count returns correct number of unread notifications")
    void getUnreadCount() {
        notificationService.createNotification(
                userA.getId(), "Test 1", "Message 1", NotificationType.SYSTEM, null, null, false);
        notificationService.createNotification(
                userA.getId(), "Test 2", "Message 2", NotificationType.HIVE_ALERT, null, null, false);

        Map<String, Long> countMap = notificationService.getUnreadCount(USER_A_PHONE);
        assertThat(countMap.get("unreadCount")).isGreaterThanOrEqualTo(2L);
    }

    @Test
    @DisplayName("Mark single notification as read")
    void markAsRead() {
        notificationService.createNotification(
                userA.getId(), "Alert", "Hive temp high", NotificationType.HIVE_ALERT, "HIVE", "1", false);

        PageResponse<NotificationResponse> before = notificationService.getMyNotifications(USER_A_PHONE, 0, 10);
        Long notifId = before.getContent().get(0).getId();

        NotificationReadResponse readRes = notificationService.markAsRead(USER_A_PHONE, notifId);
        assertThat(readRes.getIsRead()).isTrue();
        assertThat(readRes.getReadAt()).isNotNull();
    }

    @Test
    @DisplayName("Mark all notifications as read")
    void markAllAsRead() {
        notificationService.createNotification(userA.getId(), "N1", "M1", NotificationType.SYSTEM, null, null, false);
        notificationService.createNotification(userA.getId(), "N2", "M2", NotificationType.SYSTEM, null, null, false);

        Map<String, Integer> result = notificationService.markAllAsRead(USER_A_PHONE);
        assertThat(result.get("markedReadCount")).isGreaterThanOrEqualTo(2);

        Map<String, Long> unread = notificationService.getUnreadCount(USER_A_PHONE);
        assertThat(unread.get("unreadCount")).isEqualTo(0L);
    }

    @Test
    @DisplayName("API Security: User cannot mark another user's notification as read")
    void crossUserSecurityIsolation() throws Exception {
        notificationService.createNotification(
                userA.getId(), "Secret A", "For A only", NotificationType.SYSTEM, null, null, false);

        PageResponse<NotificationResponse> pageA = notificationService.getMyNotifications(USER_A_PHONE, 0, 10);
        Long notifIdA = pageA.getContent().get(0).getId();

        String tokenB = jwtService.generateToken(USER_B_PHONE, "CUSTOMER");

        // User B attempts to mark User A's notification as read -> 404 Not Found
        mockMvc.perform(put("/api/notifications/" + notifIdA + "/read")
                .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("API Endpoint: GET /api/notifications returns user notifications")
    void getNotificationsApi() throws Exception {
        notificationService.createNotification(
                userA.getId(), "Order Status", "Order confirmed", NotificationType.ORDER_STATUS, "ORDER", "HC-ORD-01",
                false);

        String tokenA = jwtService.generateToken(USER_A_PHONE, "BEEKEEPER");

        mockMvc.perform(get("/api/notifications")
                .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content[0].title", is("Order Status")));
    }

    @Test
    @DisplayName("API Endpoint: GET /api/notifications/unread-count returns badge count")
    void getUnreadCountApi() throws Exception {
        notificationService.createNotification(
                userA.getId(), "Badge Test", "Content", NotificationType.SYSTEM, null, null, false);

        String tokenA = jwtService.generateToken(USER_A_PHONE, "BEEKEEPER");

        mockMvc.perform(get("/api/notifications/unread-count")
                .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.unreadCount").exists());
    }
}
