package com.honeychain.order.mapper;

import com.honeychain.order.dto.DeliveryAddressDto;
import com.honeychain.order.dto.OrderItemResponse;
import com.honeychain.order.dto.OrderResponse;
import com.honeychain.order.entity.DeliveryAddress;
import com.honeychain.order.entity.Order;
import com.honeychain.order.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class OrderMapper {

    public OrderItemResponse toItemResponse(OrderItem item) {
        if (item == null) return null;
        return new OrderItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductNameSnapshot(),
                item.getQuantityKg(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }

    public DeliveryAddressDto toAddressDto(DeliveryAddress address) {
        if (address == null) return null;
        return new DeliveryAddressDto(
                address.getName(),
                address.getLine1(),
                address.getLine2(),
                address.getCity(),
                address.getState(),
                address.getPostalCode()
        );
    }

    public DeliveryAddress toAddressEntity(DeliveryAddressDto dto) {
        if (dto == null) return null;
        return new DeliveryAddress(
                dto.getName(),
                dto.getLine1(),
                dto.getLine2(),
                dto.getCity(),
                dto.getState(),
                dto.getPostalCode()
        );
    }

    public OrderResponse toResponse(Order order) {
        if (order == null) return null;

        List<OrderItemResponse> itemResponses = order.getOrderItems() != null
                ? order.getOrderItems().stream().map(this::toItemResponse).toList()
                : Collections.emptyList();

        OrderResponse response = new OrderResponse();
        response.setOrderNumber(order.getOrderNumber());
        response.setTotalAmount(order.getTotalAmount());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setPaymentId(order.getPaymentId());
        response.setOrderStatus(order.getOrderStatus());
        response.setFulfillmentType(order.getFulfillmentType());
        response.setDeliveryAddress(toAddressDto(order.getDeliveryAddress()));
        response.setItems(itemResponses);
        response.setCreatedAt(order.getCreatedAt());

        return response;
    }
}
