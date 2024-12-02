package com.duantn.be_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.duantn.be_project.Repository.OrderDetailsRepository;

import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@PreAuthorize("hasAnyAuthority('Admin_All_Function', 'Admin_Manage_Revenue')")
@RequestMapping("/order-details")
public class OrderDetailsController {
    // Khải nói là đéo xài
    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @GetMapping("/count-by-store-and-year")
    public ResponseEntity<List<Map<String, Object>>> getOrderDetailsByStoreAndYear() {
        List<Map<String, Object>> results = orderDetailsRepository.countOrderDetailsByStoreAndYear();
        return ResponseEntity.ok(results);
    }
}
