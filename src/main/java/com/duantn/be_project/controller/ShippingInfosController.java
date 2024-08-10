package com.duantn.be_project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Repository.ShippingInfosRepository;
import com.duantn.be_project.model.ShippingInfor;

@CrossOrigin("*")
@RestController
public class ShippingInfosController {
    @Autowired
    ShippingInfosRepository shippingInfosRepository;

    @GetMapping("/shippingInfo")
    public ResponseEntity<List<ShippingInfor>> getAll(@RequestParam("userId") Integer idUser) {
        return ResponseEntity.ok(shippingInfosRepository.findAllByUserId(idUser));
    }

}
