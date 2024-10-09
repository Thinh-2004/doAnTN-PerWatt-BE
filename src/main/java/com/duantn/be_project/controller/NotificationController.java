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
        // Tìm cửa hàng theo storeId
        List<Order> storeOptional = notificationRepository.findAll();
        return ResponseEntity.ok(storeOptional);
    }

    @GetMapping("/checkOrder/{id}")
    public ResponseEntity<List<Order>> checkNewOrder(@PathVariable("id") Integer storeId) {
        List<Order> storeOrders = notificationRepository.findAllStoreId(storeId);

        if (storeOrders.isEmpty()) {
            return ResponseEntity.noContent().build(); // Trả về HTTP 204 No Content nếu không có đơn hàng
        }

        return ResponseEntity.ok(storeOrders);
    }

}
