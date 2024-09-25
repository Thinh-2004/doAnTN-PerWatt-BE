package com.duantn.be_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/checkOrder")
    public ResponseEntity<List<Order>> findAll() {
        List<Order> storeOptional = notificationRepository.findAll();
        return ResponseEntity.ok(storeOptional);
    }

    @GetMapping("/checkOrder/{id}")
    public ResponseEntity<List<Order>> checkNewOrder(@PathVariable("id") Integer storeId) {
        List<Order> storeOrders = notificationRepository.findAllStoreId(storeId);

        if (storeOrders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(storeOrders);
    }

    @GetMapping("/deliveredOrders/{id}")
    public ResponseEntity<List<Order>> getDeliveredOrders(@PathVariable("id") Integer storeId) {
        List<Order> deliveredOrders = notificationRepository.findDeliveredOrdersByStoreId(storeId);

        if (deliveredOrders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(deliveredOrders);
    }

    // New endpoint for canceled orders
    @GetMapping("/canceledOrders/{id}")
    public ResponseEntity<List<Order>> getCanceledOrders(@PathVariable("id") Integer storeId) {
        List<Order> canceledOrders = notificationRepository.findCanceledOrdersByStoreId(storeId);

        if (canceledOrders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(canceledOrders);
    }

}
