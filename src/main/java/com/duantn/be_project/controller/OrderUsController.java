package com.duantn.be_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.duantn.be_project.Repository.OrderUsRepository;

import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
public class OrderUsController {

    //Seller
    @Autowired
    private OrderUsRepository orderUsRepository;

    @PreAuthorize("hasAnyAuthority('Seller')")
    @GetMapping("/count-orders/{storeId}")
    public ResponseEntity<?> getOrdersByStatusForStore(@PathVariable("storeId") Integer storeId) {
        try {
            List<Map<String, Object>> results = orderUsRepository.countOrdersByStatusForStore(storeId);
            if (results.isEmpty()) {
                return ResponseEntity.noContent().build(); // Return 204 if no data
            }
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("System error: " + e.getMessage());
        }
    }
}
