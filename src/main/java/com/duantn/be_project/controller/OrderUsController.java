package com.duantn.be_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.duantn.be_project.Repository.OrderUsRepository;

import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/order-us")
public class OrderUsController {

    @Autowired
    private OrderUsRepository orderUsRepository;

    @GetMapping("/count-store-processing")
    public ResponseEntity<?> getOrdersByStoreAndStatusProcessing() {
        try {
            List<Map<String, Object>> results = orderUsRepository.countOrdersByStoreAndStatusProcessing();
            if (results.isEmpty()) {
                return ResponseEntity.noContent().build(); // Trả về mã trạng thái 204 nếu không có dữ liệu
            }
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @GetMapping("/count-store-shipped")
    public ResponseEntity<?> getOrdersByStoreAndStatusShipped() {
        try {
            List<Map<String, Object>> results = orderUsRepository.countOrdersByStoreAndStatusShipped();
            if (results.isEmpty()) {
                return ResponseEntity.noContent().build(); // Trả về mã trạng thái 204 nếu không có dữ liệu
            }
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @GetMapping("/count-store-delivered")
    public ResponseEntity<?> getOrdersByStoreAndStatusDelivered() {
        try {
            List<Map<String, Object>> results = orderUsRepository.countOrdersByStoreAndStatusDelivered();
            if (results.isEmpty()) {
                return ResponseEntity.noContent().build(); // Trả về mã trạng thái 204 nếu không có dữ liệu
            }
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @GetMapping("/count-store-cancelled")
    public ResponseEntity<?> getOrdersByStoreAndStatusCancelled() {
        try {
            List<Map<String, Object>> results = orderUsRepository.countOrdersByStoreAndStatusCancelled();
            if (results.isEmpty()) {
                return ResponseEntity.noContent().build(); // Trả về mã trạng thái 204 nếu không có dữ liệu
            }
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @GetMapping("/count-store-returned")
    public ResponseEntity<?> getOrdersByStoreAndStatusReturned() {
        try {
            List<Map<String, Object>> results = orderUsRepository.countOrdersByStoreAndStatusReturned();
            if (results.isEmpty()) {
                return ResponseEntity.noContent().build(); // Trả về mã trạng thái 204 nếu không có dữ liệu
            }
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }
}
