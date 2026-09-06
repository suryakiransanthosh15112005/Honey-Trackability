package com.honeychain.order.service.impl;

import java.util.List;
import java.util.Optional;

import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.cart.entity.Cart;
import com.honeychain.cart.entity.CartItem;
import com.honeychain.cart.repository.CartRepository;
import com.honeychain.common.dto.PageResponse;
import com.honeychain.common.exception.BadRequestException;
import com.honeychain.common.exception.ResourceNotFoundException;
import com.honeychain.common.exception.UnauthorizedException;
import com.honeychain.marketplace.entity.Product;
import com.honeychain.marketplace.repository.ProductRepository;
import com.honeychain.notification.entity.NotificationType;
import com.honeychain.notification.event.NotificationEvent;
import com.honeychain.order.dto.CheckoutRequest;
import com.honeychain.order.dto.OrderResponse;
import com.honeychain.order.entity.FulfillmentType;
import com.honeychain.order.entity.Order;
import com.honeychain.order.entity.OrderItem;
import com.honeychain.order.entity.OrderStatus;
import com.honeychain.order.mapper.OrderMapper;
import com.honeychain.order.repository.OrderItemRepository;
import com.honeychain.order.repository.OrderRepository;
import com.honeychain.order.service.OrderService;
import com.honeychain.payment.dto.PaymentRequest;
import com.honeychain.payment.dto.PaymentResult;
import com.honeychain.payment.exception.PaymentFailedException;
import com.honeychain.payment.service.PaymentService;
import com.honeychain.payment.service.PaymentStatus;
import com.honeychain.user.entity.User;
import com.honeychain.user.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final BeekeeperProfileRepository beekeeperProfileRepository;
    private final PaymentService paymentService;
    private final OrderMapper orderMapper;
    private final ApplicationEventPublisher eventPublisher;

    public OrderServiceImpl(OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartRepository cartRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            BeekeeperProfileRepository beekeeperProfileRepository,
            PaymentService paymentService,
            OrderMapper orderMapper) {
        this(orderRepository, orderItemRepository, cartRepository, productRepository, userRepository,
                beekeeperProfileRepository, paymentService, orderMapper, event -> {
                });
    }

    @org.springframework.beans.factory.annotation.Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartRepository cartRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            BeekeeperProfileRepository beekeeperProfileRepository,
            PaymentService paymentService,
            OrderMapper orderMapper,
            ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.beekeeperProfileRepository = beekeeperProfileRepository;
        this.paymentService = paymentService;
        this.orderMapper = orderMapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public OrderResponse checkout(String customerPhone, CheckoutRequest request) {
        User customer = getUser(customerPhone);

        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new BadRequestException("Your cart is empty."));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BadRequestException("Your cart is empty.");
        }

        // Validate fulfillment type and address
        if (request.getFulfillmentType() == FulfillmentType.DELIVERY) {
            if (request.getDeliveryAddress() == null ||
                    request.getDeliveryAddress().getLine1() == null || request.getDeliveryAddress().getLine1().isBlank()
                    ||
                    request.getDeliveryAddress().getCity() == null || request.getDeliveryAddress().getCity().isBlank()
                    ||
                    request.getDeliveryAddress().getPostalCode() == null
                    || request.getDeliveryAddress().getPostalCode().isBlank()) {
                throw new BadRequestException(
                        "Delivery address (line1, city, postal code) is required for DELIVERY fulfillment.");
            }
        }

        // Lock and validate each product, calculate totals
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<ProductStockDeduction> deductions = new ArrayList<>();

        for (CartItem item : cart.getItems()) {
            Product product = productRepository.findByIdForUpdate(item.getProduct().getId())
                    .orElseThrow(() -> new BadRequestException("Product not found for item ID: " + item.getId()));

            if (Boolean.FALSE.equals(product.getIsActive())) {
                throw new BadRequestException("Product '" + product.getProductName() + "' is no longer available.");
            }

            if (product.getAvailableQuantityKg().compareTo(item.getQuantityKg()) < 0) {
                throw new BadRequestException("Insufficient stock for '" + product.getProductName() + "'. Available: "
                        + product.getAvailableQuantityKg() + " kg, requested: " + item.getQuantityKg() + " kg.");
            }

            BigDecimal unitPrice = product.getPricePerKg().setScale(2, java.math.RoundingMode.HALF_UP);
            BigDecimal subtotal = unitPrice.multiply(item.getQuantityKg()).setScale(2, java.math.RoundingMode.HALF_UP);
            totalAmount = totalAmount.add(subtotal);

            deductions.add(new ProductStockDeduction(product, item.getQuantityKg(), unitPrice, subtotal));
        }
        totalAmount = totalAmount.setScale(2, java.math.RoundingMode.HALF_UP);

        // Generate Order Number
        long nextOrderSeq = orderRepository.countTotalOrders() + 1;
        String orderNumber = String.format("HC-ORD-%d-%06d", LocalDate.now().getYear(), nextOrderSeq);

        // Process Mock Payment
        PaymentRequest paymentRequest = new PaymentRequest(
                customer.getId(),
                totalAmount,
                "INR",
                request.getPaymentMode() != null ? request.getPaymentMode() : "mock",
                orderNumber);

        PaymentResult paymentResult = paymentService.processPayment(paymentRequest);

        if (!paymentResult.isSuccessful()) {
            throw new PaymentFailedException(paymentResult.getMessage() != null
                    ? paymentResult.getMessage()
                    : "Payment failed. Your order has not been placed.");
        }

        // Create Order
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setCustomerId(customer.getId());
        order.setTotalAmount(totalAmount);
        order.setPaymentStatus(paymentResult.getStatus());
        order.setPaymentId(paymentResult.getPaymentId());
        order.setOrderStatus(OrderStatus.CONFIRMED);
        order.setFulfillmentType(request.getFulfillmentType());

        if (request.getFulfillmentType() == FulfillmentType.DELIVERY) {
            order.setDeliveryAddress(orderMapper.toAddressEntity(request.getDeliveryAddress()));
        }

        Order savedOrder = orderRepository.save(order);

        // Create Order Items & Deduct Stock
        List<OrderItem> orderItems = new ArrayList<>();
        for (ProductStockDeduction deduction : deductions) {
            Product product = deduction.product;
            OrderItem orderItem = new OrderItem(
                    savedOrder,
                    product.getId(),
                    product.getProductName(),
                    deduction.quantityKg,
                    deduction.unitPrice,
                    deduction.subtotal);
            orderItems.add(orderItem);

            // Deduct stock
            BigDecimal remainingStock = product.getAvailableQuantityKg().subtract(deduction.quantityKg);
            product.setAvailableQuantityKg(remainingStock);
            if (remainingStock.compareTo(BigDecimal.ZERO) <= 0) {
                product.setIsActive(false);
            }
            productRepository.save(product);
        }

        orderItemRepository.saveAll(orderItems);
        savedOrder.setOrderItems(orderItems);

        // Clear cart
        cart.clearItems();
        cartRepository.save(cart);

        // Notify Customer of order confirmation
        eventPublisher.publishEvent(new NotificationEvent(
                customer.getId(),
                "Order Confirmed",
                "Your order " + orderNumber + " has been confirmed.",
                NotificationType.ORDER_STATUS,
                "ORDER",
                orderNumber,
                false));

        // Notify affected Beekeepers of new order
        Set<Long> notifiedBeekeeperUserIds = new HashSet<>();
        for (ProductStockDeduction deduction : deductions) {
            Long beekeeperProfileId = deduction.product.getBeekeeperProfile().getId();
            BeekeeperProfile bkProfile = beekeeperProfileRepository.findById(beekeeperProfileId).orElse(null);
            if (bkProfile != null && notifiedBeekeeperUserIds.add(bkProfile.getUserId())) {
                eventPublisher.publishEvent(new NotificationEvent(
                        bkProfile.getUserId(),
                        "New Honey Order",
                        String.format("New order %s received containing your honey products.", orderNumber),
                        NotificationType.NEW_ORDER,
                        "ORDER",
                        orderNumber,
                        true));
            }
        }

        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getMyOrders(String customerPhone, Pageable pageable) {
        User customer = getUser(customerPhone);
        Page<Order> page = orderRepository.findAllByCustomerIdOrderByCreatedAtDesc(customer.getId(), pageable);
        List<OrderResponse> content = page.getContent().stream().map(orderMapper::toResponse).toList();
        return PageResponse.of(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getMyOrderByNumber(String customerPhone, String orderNumber) {
        User customer = getUser(customerPhone);
        Order order = orderRepository.findByOrderNumberAndCustomerId(orderNumber, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderNumber));
        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse cancelOrder(String customerPhone, String orderNumber) {
        User customer = getUser(customerPhone);
        Order order = orderRepository.findByOrderNumberAndCustomerId(orderNumber, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderNumber));

        if (order.getOrderStatus() != OrderStatus.CONFIRMED) {
            throw new BadRequestException(
                    "Only CONFIRMED orders can be cancelled. Current status: " + order.getOrderStatus());
        }

        order.setOrderStatus(OrderStatus.CANCELLED);

        // Restore inventory
        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                productRepository.findById(item.getProductId()).ifPresent(product -> {
                    product.setAvailableQuantityKg(product.getAvailableQuantityKg().add(item.getQuantityKg()));
                    product.setIsActive(true);
                    productRepository.save(product);
                });
            }
        }

        Order saved = orderRepository.save(order);
        return orderMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getBeekeeperOrders(String beekeeperPhone, Pageable pageable) {
        Optional<BeekeeperProfile> profileOpt = beekeeperProfileRepository.findByUserPhoneNumber(beekeeperPhone);
        if (profileOpt.isEmpty()) {
            return PageResponse.of(Page.empty(pageable), List.of());
        }
        BeekeeperProfile profile = profileOpt.get();
        Page<Order> page = orderRepository.findOrdersByBeekeeperProfileId(profile.getId(), pageable);
        List<OrderResponse> content = page.getContent().stream().map(orderMapper::toResponse).toList();
        return PageResponse.of(page, content);
    }

    @Override
    public OrderResponse updateBeekeeperOrderStatus(String beekeeperPhone, String orderNumber, OrderStatus newStatus) {
        BeekeeperProfile profile = beekeeperProfileRepository.findByUserPhoneNumber(beekeeperPhone)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Beekeeper profile not found for phone: " + beekeeperPhone));

        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderNumber));

        // Ownership verification: ensure at least one item belongs to this beekeeper
        boolean ownsItem = false;
        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                Product product = productRepository.findById(item.getProductId()).orElse(null);
                if (product != null && product.getBeekeeperProfile().getId().equals(profile.getId())) {
                    ownsItem = true;
                    break;
                }
            }
        }

        if (!ownsItem) {
            throw new UnauthorizedException("You do not have permission to manage this order.");
        }

        OrderStatus currentStatus = order.getOrderStatus();
        validateStatusTransition(currentStatus, newStatus);

        order.setOrderStatus(newStatus);
        Order saved = orderRepository.save(order);

        // Notify Customer of status update
        eventPublisher.publishEvent(new NotificationEvent(
                saved.getCustomerId(),
                "Order Status Updated",
                String.format("Your order %s status has been updated to %s.", orderNumber, newStatus),
                NotificationType.ORDER_STATUS,
                "ORDER",
                orderNumber,
                true));

        return orderMapper.toResponse(saved);
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        if (current == OrderStatus.DELIVERED || current == OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot update an order that is already " + current);
        }

        boolean isValid = switch (current) {
            case CONFIRMED -> next == OrderStatus.PACKED || next == OrderStatus.CANCELLED;
            case PACKED -> next == OrderStatus.SHIPPED;
            case SHIPPED -> next == OrderStatus.DELIVERED;
            default -> false;
        };

        if (!isValid) {
            throw new BadRequestException("Invalid status transition from " + current + " to " + next);
        }
    }

    private User getUser(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer account not found: " + phoneNumber));
    }

    private record ProductStockDeduction(Product product, BigDecimal quantityKg, BigDecimal unitPrice,
            BigDecimal subtotal) {
    }
}
