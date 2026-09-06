package com.honeychain.notification.controller;

import com.honeychain.common.dto.ApiResponse;
import com.honeychain.common.dto.PageResponse;
import com.honeychain.notification.dto.NotificationReadResponse;
import com.honeychain.notification.dto.NotificationResponse;
import com.honeychain.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Endpoints for user notifications and unread badges")
@SecurityRequirement(name = "Bearer Authentication")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "Get My Notifications", description = "Paginated list of notifications for the authenticated user ordered newest first")
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> getMyNotifications(
            Authentication authentication,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        PageResponse<NotificationResponse> response = notificationService
                .getMyNotifications(authentication.getName(), page, size);
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved successfully", response));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get Unread Count", description = "Retrieve count of unread notifications for the notification bell badge")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(Authentication authentication) {
        Map<String, Long> result = notificationService.getUnreadCount(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Unread count retrieved", result));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark Notification as Read", description = "Mark a specific notification belonging to the authenticated user as read")
    public ResponseEntity<ApiResponse<NotificationReadResponse>> markAsRead(
            Authentication authentication,
            @PathVariable Long id) {
        NotificationReadResponse response = notificationService.markAsRead(authentication.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", response));
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark All Notifications as Read", description = "Mark all unread notifications for the authenticated user as read")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> markAllAsRead(Authentication authentication) {
        Map<String, Integer> result = notificationService.markAllAsRead(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", result));
    }
}
