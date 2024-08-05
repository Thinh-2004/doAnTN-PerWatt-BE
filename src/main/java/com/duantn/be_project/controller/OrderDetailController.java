package com.duantn.be_project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Repository.OrderDetailRepository;
import com.duantn.be_project.Repository.OrderRepository;
import com.duantn.be_project.model.OrderDetail;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@CrossOrigin("*")
@RestController
public class OrderDetailController {
    @Autowired
    OrderDetailRepository orderDetailRepository;

    // GetAll
    @GetMapping("/orderDetail")
    public ResponseEntity<List<OrderDetail>> getAll(Model model) {
        return ResponseEntity.ok(orderDetailRepository.findAll());
    }

    // // GetAllById
    // @GetMapping("/orderDetail/{id}")
    // public ResponseEntity<OrderDetail> getById(@PathVariable("id") Integer id) {
    //     OrderDetail orderDetail = orderDetailRepository.findById(id).orElseThrow();
    //     if (orderDetailRepository == null) {
    //         return ResponseEntity.notFound().build();
    //     }
    //     return ResponseEntity.ok(orderDetail);
    // }
    
    //GetAllOrderDetail By id Order
    @GetMapping("/orderDetail/{id}")
    public ResponseEntity<List<OrderDetail>> getIdOrderDetailByIdOrder(@PathVariable("id") Integer id) {
        List<OrderDetail> orderDetails = orderDetailRepository.findAllOrderDetailByIdOrder(id);
        if (orderDetails.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orderDetails);
    }
    

    // // Post
    // @PostMapping("/order")
    // public ResponseEntity<Order> post(@RequestBody Order order) {
    //     // TODO: process POST request
    //     if (orderRepository.existsById(order.getId())) {
    //         return ResponseEntity.badRequest().build();
    //     }
    //     return ResponseEntity.ok(orderRepository.save(order));
    // }

    // // Put
    // @PutMapping("order/{id}")
    // public ResponseEntity<Order> put(@PathVariable("id") Integer id,
    //         @RequestBody Order order) {
    //     // TODO: process PUT request
    //     if (!orderRepository.existsById(id)) {
    //         return ResponseEntity.notFound().build();
    //     }
    //     return ResponseEntity.ok(orderRepository.save(order));
    // }

    // @DeleteMapping("/order/{id}")
    // public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
    //     // TODO: process PUT request
    //     if (!orderRepository.existsById(id)) {
    //         return ResponseEntity.notFound().build();
    //     }
    //     orderRepository.deleteById(id);
    //     return ResponseEntity.ok().build();
    // }

}
