package org.golovko.telegramshop.service;

import org.golovko.telegramshop.domain.Customer;
import org.golovko.telegramshop.domain.OrderCart;
import org.golovko.telegramshop.domain.OrderItem;
import org.golovko.telegramshop.domain.OrderStatus;
import org.golovko.telegramshop.domain.model.CartItem;
import org.golovko.telegramshop.repository.OrderItemRepository;
import org.golovko.telegramshop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Transactional
    public List<OrderCart> getAllOrders(long chatId) {
        return orderRepository.findOrderByChatIdOrderByCreatedDateDesc(chatId);
    }

    @Transactional
    public OrderCart createOrder(Customer customer, List<CartItem> cartItems) {
        OrderCart order = new OrderCart();
        order.setCreatedDate(LocalDateTime.now());
        order.setCustomer(customer);
        order.setPaymentType(customer.getPaymentType());
        order.setStatus(OrderStatus.WAITING);
        order.setTotalSum(calculateTotalPrice(cartItems));
        order.setOrderNumber(generateOrderNumber());

        order = orderRepository.save(order);

        fromCartItems(cartItems, order);

        return orderRepository.save(order);
    }

    private String generateOrderNumber() {
        LocalDate now = LocalDate.now();
        String fourChars = UUID.randomUUID().toString().substring(9, 13);
        return now + "/" + fourChars;
    }

    public double calculateTotalPrice(List<CartItem> cartItems) {
        double totalPrice = 0;

        for (CartItem cartItem : cartItems) {
            totalPrice += cartItem.getTotalPrice();
        }

        return totalPrice;
    }

    private List<OrderItem> fromCartItems(List<CartItem> cartItems, OrderCart order) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrderCart(order);
            orderItems.add(orderItem);
        }

        orderItemRepository.saveAll(orderItems);
        return orderItems;
    }

    public OrderCart getOrderById(UUID orderId) {
        return orderRepository.findById(orderId).get();
    }
}
