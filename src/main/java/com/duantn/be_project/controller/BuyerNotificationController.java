package com.duantn.be_project.controller;

import com.duantn.be_project.Repository.BuyerNotificationRepository;
import com.duantn.be_project.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
public class BuyerNotificationController {

    @Autowired
    BuyerNotificationRepository buyerNotificationRepository;

    // API kiểm tra đơn hàng đang "Chờ giao hàng"
    @GetMapping("/checkOrderReadyToShip/{idStore}")
    public ResponseEntity<List<Order>> checkReadyToShipOrders(@PathVariable("idStore") Integer storeId) {
        List<Order> readyToShipOrders = buyerNotificationRepository.findAllReadyToShipOrders(storeId);

        if (readyToShipOrders.isEmpty()) {
            return ResponseEntity.noContent().build(); // HTTP 204 No Content nếu không có đơn hàng
        }

        return ResponseEntity.ok(readyToShipOrders);
    }

    // API kiểm tra đơn hàng "Đã hủy"
    @GetMapping("/checkCanceledOrders/{idStore}")
    public ResponseEntity<List<Order>> checkCanceledOrders(@PathVariable("idStore") Integer storeId) {
        List<Order> canceledOrders = buyerNotificationRepository.findAllCanceledOrders(storeId);

        if (canceledOrders.isEmpty()) {
            return ResponseEntity.noContent().build(); // HTTP 204 No Content nếu không có đơn hàng
        }

        return ResponseEntity.ok(canceledOrders);
    }

}
