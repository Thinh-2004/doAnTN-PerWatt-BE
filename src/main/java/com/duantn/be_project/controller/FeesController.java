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
        if (!feeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        fee.setId(id);
        Fee updatedFee = feeRepository.save(fee);
        return ResponseEntity.ok(updatedFee);
    }

    // Delete a Fee
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFee(@PathVariable Integer id) {
        if (!feeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        feeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Get total revenue after tax
    @GetMapping("/{id}/total-revenue")
    public ResponseEntity<Float> getTotalRevenueAfterTax(@PathVariable Integer id) {
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        Float totalRevenue = feeRepository.calculateTotalRevenueAfterTax(id, currentYear);
        return ResponseEntity.ok(totalRevenue);
    }
}
