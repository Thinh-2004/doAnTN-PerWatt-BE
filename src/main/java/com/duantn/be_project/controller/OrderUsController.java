package com.duantn.be_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.duantn.be_project.Repository.OrderUsRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
public class OrderUsController {

    // Seller
    @Autowired
    private OrderUsRepository orderUsRepository;

    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop')")
    @GetMapping("/count-orders/{storeId}")
    public ResponseEntity<?> getOrdersByStatusForStore(
            @PathVariable("storeId") Integer storeId,
            @RequestParam(value = "startDate", required = false) Date startDate,
            @RequestParam(value = "endDate", required = false) Date endDate) {

        try {
            // Nếu startDate và endDate không có trong query, đặt giá trị mặc định
            if (startDate == null) {
                // Lấy ngày 7 ngày trước
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                startDate = calendar.getTime();
            }
            if (endDate == null) {
                // Nếu không có endDate, sử dụng ngày hiện tại
                endDate = new Date();
            }

            // Gọi phương thức countOrdersByStatusForStore trong repository
            List<Map<String, Object>> results = orderUsRepository.countOrdersByStatusForStore(storeId, startDate,
                    endDate);
            if (results.isEmpty()) {
                return ResponseEntity.noContent().build(); // Return 204 if no data
            }
            return ResponseEntity.ok(results); // Return 200 with results
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("System error: " + e.getMessage());
        }
    }

}