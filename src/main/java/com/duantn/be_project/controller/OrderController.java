package com.duantn.be_project.controller;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Repository.OrderRepository;
import com.duantn.be_project.Repository.ProductDetailRepository;
import com.duantn.be_project.model.Order;
import com.duantn.be_project.model.OrderDetail;
import com.duantn.be_project.model.ProductDetail;

import jakarta.transaction.Transactional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@CrossOrigin("*")
@RestController
public class OrderController {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductDetailRepository productRepository;

    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop', 'Buyer_Manage_Buyer')")
    @GetMapping("/order")
    public ResponseEntity<List<Order>> getAll(Model model) {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop', 'Buyer_Manage_Buyer')")
    @GetMapping("/order/{id}")
    public ResponseEntity<Order> getById(@PathVariable("id") Integer id) {
        Order order = orderRepository.findById(id).orElseThrow();
        if (orderRepository == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop', 'Buyer_Manage_Buyer')")
    @GetMapping("/orderFill/{id}")
    public ResponseEntity<List<Order>> getById2(@PathVariable("id") Integer id) {
        List<Order> orders = orderRepository.findAllByUserId(id);
        if (orders == null || orders.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Order> sortedOrders = orders.stream()
                .sorted((o1, o2) -> o2.getId().compareTo(o1.getId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(sortedOrders);
    }

    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop', 'Buyer_Manage_Buyer')")
    @GetMapping("/orderSeller/{id}")
    public ResponseEntity<List<Order>> getOrdersByStoreId(@PathVariable("id") Integer id) {
        List<Order> orders = orderRepository.findAllByStoreId(id);
        if (orders == null || orders.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Order> sortedOrders = orders.stream()
                .sorted((o1, o2) -> o2.getId().compareTo(o1.getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(sortedOrders);
    }

    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop', 'Buyer_Manage_Buyer')")
    @PutMapping("/order/{id}/status")
    @Transactional
    public ResponseEntity<Order> updateOrderStatus(@PathVariable("id") Integer id,
            @RequestBody Map<String, String> statusUpdate) {
        if (!orderRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderRepository.findById(id).orElseThrow();
        String newStatus = statusUpdate.get("status");
        String note = statusUpdate.get("note");
        String receivedate = statusUpdate.get("receivedate");

        String oldStatus = order.getOrderstatus();
        order.setOrderstatus(newStatus);
        order.setNote(note);

        if (receivedate != null) {
            order.setReceivedate(Date.from(Instant.parse(receivedate)));
        }

        orderRepository.save(order);

        if ("Hủy".equals(newStatus) && !"Hủy".equals(oldStatus)) {
            for (OrderDetail detail : order.getOrderdetails()) {
                ProductDetail product = detail.getProductDetail();
                int quantity = detail.getQuantity();
                product.setQuantity(product.getQuantity() + quantity);
                productRepository.save(product);
            }
        }

        return ResponseEntity.ok(order);
    }

    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop', 'Buyer_Manage_Buyer')")
    @DeleteMapping("/order/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        if (!orderRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        orderRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

}