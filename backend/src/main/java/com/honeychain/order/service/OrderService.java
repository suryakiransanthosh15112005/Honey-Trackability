package com.honeychain.order.service;

import com.honeychain.common.dto.PageResponse;
import com.honeychain.order.dto.CheckoutRequest;
import com.honeychain.order.dto.OrderResponse;
import com.honeychain.order.entity.OrderStatus;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse checkout(String customerPhone, CheckoutRequest request);

    PageResponse<OrderResponse> getMyOrders(String customerPhone, Pageable pageable);

    OrderResponse getMyOrderByNumber(String customerPhone, String orderNumber);

    OrderResponse cancelOrder(String customerPhone, String orderNumber);

    PageResponse<OrderResponse> getBeekeeperOrders(String beekeeperPhone, Pageable pageable);

    OrderResponse updateBeekeeperOrderStatus(String beekeeperPhone, String orderNumber, OrderStatus newStatus);
}
