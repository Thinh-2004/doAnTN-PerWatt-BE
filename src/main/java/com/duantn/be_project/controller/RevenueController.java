package com.duantn.be_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.duantn.be_project.Repository.StoreRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("/revenue")
public class RevenueController {

    @Autowired
    private StoreRepository storeRepository;

    @GetMapping("/total-revenue-by-store")
    public ResponseEntity<List<Map<String, Object>>> getTotalRevenueByStore() {
        List<Object[]> results = storeRepository.findTotalRevenueByStore();
        List<Map<String, Object>> response = results.stream()
                .map(result -> Map.of(
                        "storeName", result[0],
                        "totalRevenue", result[1]
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/total-stores")
    public ResponseEntity<Long> getTotalStores() {
        Long totalStores = storeRepository.countTotalStores();
        return ResponseEntity.ok(totalStores);
    }

    @GetMapping("/stores-by-year")
    public ResponseEntity<List<Map<String, Object>>> getStoresByYear() {
        List<Map<String, Object>> results = storeRepository.countStoresByYear();
        return ResponseEntity.ok(results);
    }
}
