package com.duantn.be_project.controller;

import com.duantn.be_project.Repository.OrderDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/order-details")
public class OrderDetailsController {

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @GetMapping("/count-by-store-and-year")
    public ResponseEntity<List<Map<String, Object>>> getOrderDetailsByStoreAndYear() {
        List<Map<String, Object>> results = orderDetailsRepository.countOrderDetailsByStoreAndYear();
        return ResponseEntity.ok(results);
    }
}
