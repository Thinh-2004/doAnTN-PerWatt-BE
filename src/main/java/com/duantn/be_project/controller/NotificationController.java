package com.duantn.be_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Repository.NotificationRepository;
import com.duantn.be_project.model.Order;
import java.util.List;

@CrossOrigin("*")
@RestController
public class NotificationController {

    @Autowired
    NotificationRepository notificationRepository;

    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop', 'Buyer_Manage_Buyer')")
    @GetMapping("/checkOrder")
    public ResponseEntity<List<Order>> findAll() {
        // Tìm cửa hàng theo storeId
        List<Order> storeOptional = notificationRepository.findAll();
        return ResponseEntity.ok(storeOptional);
    }

    // API xử lí seller
    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop', 'Buyer_Manage_Buyer')")
    @GetMapping("/checkOrderSeller/{id}")
    public ResponseEntity<List<Order>> checkNewOrder(@PathVariable("id") Integer storeId) {
        List<Order> storeOrders = notificationRepository.findAllStoreId(storeId);

        if (storeOrders.isEmpty()) {
            return ResponseEntity.noContent().build(); // Trả về HTTP 204 No Content nếu không có đơn hàng
        }

        return ResponseEntity.ok(storeOrders);
    }

    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop', 'Buyer_Manage_Buyer')")
    @GetMapping("/deliveredOrders/{id}")
    public ResponseEntity<List<Order>> getDeliveredOrders(@PathVariable("id") Integer storeId) {
        List<Order> deliveredOrders = notificationRepository.findDeliveredOrdersByStoreId(storeId);

        if (deliveredOrders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(deliveredOrders);
    }

    // New endpoint for canceled orders
    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop', 'Buyer_Manage_Buyer')")
    @GetMapping("/canceledOrders/{id}")
    public ResponseEntity<List<Order>> getCanceledOrders(@PathVariable("id") Integer storeId) {
        List<Order> canceledOrders = notificationRepository.findCanceledOrdersByStoreId(storeId);

        if (canceledOrders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(canceledOrders);
    }

    // API xử lí buyer
    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop', 'Buyer_Manage_Buyer')")
    @GetMapping("/checkOrderBuyer/{userId}")
    public ResponseEntity<List<Order>> checkReadyToShipOrCanceledOrders(@PathVariable("userId") Integer userId) {
        List<Order> orders = notificationRepository.findAllReadyToShipOrCanceledOrders(userId);

        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build(); // HTTP 204 No Content nếu không có đơn hàng
        }

        return ResponseEntity.ok(orders);
    }

}
