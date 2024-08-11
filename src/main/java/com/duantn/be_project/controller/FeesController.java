package com.duantn.be_project.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.duantn.be_project.Repository.FeeRepository;
import com.duantn.be_project.model.Fee;

@CrossOrigin("*")
@RestController
@RequestMapping("/fees")
public class FeesController {

    @Autowired
    private FeeRepository feeRepository;

    // Get all Fees
    @GetMapping
    public ResponseEntity<List<Fee>> getAllFees() {
        List<Fee> fees = feeRepository.findAll();
        return ResponseEntity.ok(fees);
    }

    // Get a Fee by ID
    @GetMapping("/{id}")
    public ResponseEntity<Fee> getFeeById(@PathVariable Integer id) {
        Optional<Fee> fee = feeRepository.findById(id);
        return fee.map(ResponseEntity::ok)
                  .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Save a new Fee
    @PostMapping
    public ResponseEntity<Fee> saveFee(@RequestBody Fee fee) {
        Fee savedFee = feeRepository.save(fee);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFee);
    }

    // Update an existing Fee
    @PutMapping("/{id}")
    public ResponseEntity<Fee> updateFee(@PathVariable Integer id, @RequestBody Fee fee) {
        Optional<Fee> existingFeeOptional = feeRepository.findById(id);
        if (!existingFeeOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
    
        Fee existingFee = existingFeeOptional.get();
        existingFee.setTaxmoney(fee.getTaxmoney()); // Cập nhật taxmoney
        // Không thay đổi commission
    
        Fee updatedFee = feeRepository.save(existingFee);
        return ResponseEntity.ok(updatedFee);
    }

    // Get Store and Fee Details
    @GetMapping("/store-fee-details")
    public ResponseEntity<List<Object[]>> getStoreAndFeeDetails() {
        List<Object[]> results = feeRepository.findStoreAndFeeDetails();
        return ResponseEntity.ok(results);
    }

    // Get Store Revenue
    @GetMapping("/store-revenue")
    public ResponseEntity<List<Object[]>> getStoreRevenue() {
        List<Object[]> results = feeRepository.getStoreRevenue();
        return ResponseEntity.ok(results);
    }
}
