package com.duantn.be_project.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
        List<ShippingInfor> sortedShippingInfos = shippingInfosRepository.findAllByUserId(idUser).stream()
                .sorted((o1, o2) -> o2.getId().compareTo(o1.getId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(sortedShippingInfos);
    }

    @PostMapping("/shippingInfoCreate")
    public ResponseEntity<ShippingInfor> post(@RequestBody ShippingInfor shippingInfor) {
        ShippingInfor savedShippingInfor = shippingInfosRepository.save(shippingInfor);
        return ResponseEntity.ok(savedShippingInfor);
    }

    @PutMapping("/shippingInfoUpdate/{id}")
    public ResponseEntity<ShippingInfor> update(@PathVariable Integer id, @RequestBody ShippingInfor shippingInfor) {
        shippingInfor.setId(id);
        ShippingInfor updatedShippingInfor = shippingInfosRepository.save(shippingInfor);
        return ResponseEntity.ok(updatedShippingInfor);
    }

    @DeleteMapping("/shippingInfoDelete/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        shippingInfosRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}