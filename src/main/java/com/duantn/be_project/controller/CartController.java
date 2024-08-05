package com.duantn.be_project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import com.duantn.be_project.Repository.CartRepository;
import com.duantn.be_project.model.CartItem;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@CrossOrigin("*")
@RestController
public class CartController {
    @Autowired
    CartRepository cartRepository;

    // GetAll
    @GetMapping("/cart")
    public ResponseEntity<List<CartItem>> getAll(Model model) {
        return ResponseEntity.ok(cartRepository.findAll());
    }

    // GetAllById
    @GetMapping("/cart/{id}")
    public ResponseEntity<List<CartItem>> getById(@PathVariable("id") Integer id) {
    List<CartItem> cartItems = cartRepository.findAllCartItemlByIdUser(id);
    if (cartRepository == null) {
    return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(cartItems);
    }

    // // Post
    // @PostMapping("/order")
    // public ResponseEntity<Order> post(@RequestBody Order order) {
    // // TODO: process POST request
    // if (orderRepository.existsById(order.getId())) {
    // return ResponseEntity.badRequest().build();
    // }
    // return ResponseEntity.ok(orderRepository.save(order));
    // }

    // // Put
    // @PutMapping("order/{id}")
    // public ResponseEntity<Order> put(@PathVariable("id") Integer id,
    // @RequestBody Order order) {
    // // TODO: process PUT request
    // if (!orderRepository.existsById(id)) {
    // return ResponseEntity.notFound().build();
    // }
    // return ResponseEntity.ok(orderRepository.save(order));
    // }

    // @DeleteMapping("/order/{id}")
    // public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
    // // TODO: process PUT request
    // if (!orderRepository.existsById(id)) {
    // return ResponseEntity.notFound().build();
    // }
    // orderRepository.deleteById(id);
    // return ResponseEntity.ok().build();
    // }

}
