package com.duantn.be_project.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.duantn.be_project.Repository.CartRepository;
import com.duantn.be_project.Repository.OrderDetailRepository;
import com.duantn.be_project.Repository.OrderRepository;
import com.duantn.be_project.Repository.ProductDetailRepository;
import com.duantn.be_project.model.CartItem;
import com.duantn.be_project.model.Order;
import com.duantn.be_project.model.OrderDetail;
import com.duantn.be_project.model.Request_Response.OrderRequest;
import com.duantn.be_project.model.PaymentMethod;
import com.duantn.be_project.model.ProductDetail;

@RestController
@CrossOrigin("*")
public class OrderDetailController {

    @Autowired
    OrderDetailRepository orderDetailRepository; // Inject repository để làm việc với OrderDetail
    @Autowired
    OrderRepository orderRepository; // Inject repository để làm việc với Order
    @Autowired
    CartRepository cartRepository; // Inject repository để làm việc với CartItem
    @Autowired
    ProductDetailRepository productDetailRepository;

    @PreAuthorize("hasAnyAuthority('Seller', 'Buyer')")
    @GetMapping("/orderDetail")
    public ResponseEntity<List<OrderDetail>> getAll() {
        return ResponseEntity.ok(orderDetailRepository.findAll());
    }

    @PreAuthorize("hasAnyAuthority('Seller', 'Buyer')")
    @GetMapping("/orderDetail/{id}")
    public ResponseEntity<List<OrderDetail>> getAllById(@PathVariable("id") Integer id) {
        List<OrderDetail> orderDetails = orderDetailRepository.findAllOrderDetailByIdOrder(id);

        return ResponseEntity.ok(orderDetails);
    }

    @PreAuthorize("hasAnyAuthority('Seller', 'Buyer')")
    @PostMapping("/orderDetail/update/{id}")
    public ResponseEntity<List<OrderDetail>> updateOrderDetail(
            @PathVariable("id") Integer id,
            @RequestBody Map<String, String> requestBody) {

        Optional<OrderDetail> optionalOrderDetail = orderDetailRepository.findById(id);

        if (optionalOrderDetail.isPresent()) {
            OrderDetail orderDetail = optionalOrderDetail.get();

            String status = requestBody.get("status");
            if (status != null) {
                orderDetail.setStatus(status);
            }

            orderDetailRepository.save(orderDetail);

            List<OrderDetail> orderDetails = List.of(orderDetail);
            return ResponseEntity.ok(orderDetails);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyAuthority('Seller', 'Buyer')")
    @GetMapping("/orderDetailSeller/{id}")
    public ResponseEntity<List<OrderDetail>> getAllByIdSeller(@PathVariable("id") Integer id) {
        List<OrderDetail> orderDetails = orderDetailRepository.findAllOrderDetailByIdOrder(id);

        return ResponseEntity.ok(orderDetails);
    }

    @PreAuthorize("hasAnyAuthority('Seller', 'Buyer')")
    @PostMapping("/api/orderCreate")
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        Order order = new Order();
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(1);
        order.setUser(orderRequest.getOrder().getUser());
        order.setPaymentmethod(paymentMethod);
        order.setShippinginfor(orderRequest.getOrder().getShippinginfor());
        order.setStore(orderRequest.getOrder().getStore());
        order.setPaymentdate(orderRequest.getOrder().getPaymentdate());
        order.setOrderstatus(orderRequest.getOrder().getOrderstatus());
        order.setTotalamount(orderRequest.getOrder().getTotalamount());

        Order savedOrder = orderRepository.save(order);

        if (orderRequest.getOrderDetails() != null) {
            for (OrderDetail detailRequest : orderRequest.getOrderDetails()) {
                OrderDetail detail = new OrderDetail();
                detail.setOrder(savedOrder);
                detail.setProductDetail(detailRequest.getProductDetail());
                detail.setQuantity(detailRequest.getQuantity());
                detail.setPrice(detailRequest.getPrice());
                detail.setStatus("");
                orderDetailRepository.save(detail);

                ProductDetail product = productDetailRepository.findById(detailRequest.getProductDetail().getId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

                if (product.getQuantity() < detailRequest.getQuantity()) {
                    return ResponseEntity.badRequest().body(null);
                }
                product.setQuantity(product.getQuantity() - detailRequest.getQuantity());
                productDetailRepository.save(product);

                CartItem cartItem = cartRepository.findByProductDetailAndUser(detailRequest.getProductDetail(),
                        savedOrder.getUser());
                if (cartItem != null) {
                    cartRepository.delete(cartItem);
                }
            }
        }

        return ResponseEntity.ok(savedOrder);
    }

}