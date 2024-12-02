package com.duantn.be_project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Repository.PaymentMethodRepository;
import com.duantn.be_project.model.PaymentMethod;

@CrossOrigin("*")
@RestController
public class PaymentMethodController {
    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop', 'Buyer_Manage_Buyer')")
    @GetMapping("/paymentMethod")
    public ResponseEntity<List<PaymentMethod>> getAll(Model model) {
        return ResponseEntity.ok(paymentMethodRepository.findAll());
    }

}
