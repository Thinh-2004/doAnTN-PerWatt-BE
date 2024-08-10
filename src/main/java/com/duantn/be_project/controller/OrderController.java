package com.duantn.be_project.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Repository.OrderRepository;
import com.duantn.be_project.Repository.ProductRepository;
import com.duantn.be_project.model.Order;
import com.duantn.be_project.model.OrderDetail;
import com.duantn.be_project.model.Product;

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
    ProductRepository productRepository;

    // GetAll
    @GetMapping("/order")
    public ResponseEntity<List<Order>> getAll(Model model) {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    // GetAllById
    @GetMapping("/order/{id}")
    public ResponseEntity<Order> getById(@PathVariable("id") Integer id) {
        Order order = orderRepository.findById(id).orElseThrow();
        if (orderRepository == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    @GetMapping("/orderFill/{id}")
    public ResponseEntity<List<Order>> getById2(@PathVariable("id") Integer id) {
        List<Order> orders = orderRepository.findAllByUserId(id);
        if (orders == null || orders.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Sắp xếp danh sách theo id giảm dần
        List<Order> sortedOrders = orders.stream()
                .sorted((o1, o2) -> o2.getId().compareTo(o1.getId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(sortedOrders);
    }

    @GetMapping("/orderSeller/{id}")
    public ResponseEntity<List<Order>> getOrdersByStoreId(@PathVariable("id") Integer id) {
        List<Order> orders = orderRepository.findAllByStoreId(id);
        if (orders == null || orders.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // Sắp xếp danh sách theo id giảm dần
        List<Order> sortedOrders = orders.stream()
                .sorted((o1, o2) -> o2.getId().compareTo(o1.getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(sortedOrders);
    }

    // // Post
    // @PostMapping("/order")
    // public ResponseEntity<Order> post(@RequestBody Order order) {
// User user = new User();
    // PaymentMethod paymentMethod = new PaymentMethod();
    // ShippingInfor shippingInfor = new ShippingInfor();
    // Fee fee = new Fee();
    // Store store = new Store();
    // order.setPaymentdate(LocalDateTime.now());// Thiết lập thời gian tạo
    // order.setOrderstatus("Đang chờ duyệt");
    // user.setId(1);
    // paymentMethod.setId(1);
    // shippingInfor.setId(1);
    // fee.setId(1);
    // store.setId(1);
    // order.setUser(user);
    // order.setPaymentmethod(paymentMethod);
    // order.setShippinginfor(shippingInfor);
    // order.setFee(fee);
    // order.setStore(store);
    // Order savedOrder = orderRepository.save(order);
    // return ResponseEntity.ok(savedOrder);
    // }

    @PutMapping("/order/{id}/status")
    @Transactional
    public ResponseEntity<Order> updateOrderStatus(@PathVariable("id") Integer id,
            @RequestBody Map<String, String> statusUpdate) {
        if (!orderRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderRepository.findById(id).orElseThrow();
        String newStatus = statusUpdate.get("status");
        String oldStatus = order.getOrderstatus();
        order.setOrderstatus(newStatus);
        orderRepository.save(order);

        // Nếu đơn hàng bị hủy, cập nhật số lượng sản phẩm
        if ("Hủy".equals(newStatus) && !"Hủy".equals(oldStatus)) {
            for (OrderDetail detail : order.getOrderdetails()) {
                Product product = detail.getProduct();
                int quantity = detail.getQuantity();
                product.setQuantity(product.getQuantity() + quantity);
                productRepository.save(product);
            }
        }

        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/order/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        if (!orderRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        orderRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

}