package com.duantn.be_project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    // Lấy tất cả các chi tiết đơn hàng
    @GetMapping("/orderDetail")
    public ResponseEntity<List<OrderDetail>> getAll() {
        return ResponseEntity.ok(orderDetailRepository.findAll());
    }

    // Lấy tất cả các chi tiết đơn hàng theo id đơn hàng
    @GetMapping("/orderDetail/{id}")
    public ResponseEntity<List<OrderDetail>> getAllById(@PathVariable("id") Integer id) {
        List<OrderDetail> orderDetails = orderDetailRepository.findAllOrderDetailByIdOrder(id);
        if (orderDetails.isEmpty()) {
            return ResponseEntity.noContent().build(); // Trả về HTTP 204 nếu không có dữ liệu
        }
        return ResponseEntity.ok(orderDetails); // Trả về danh sách chi tiết đơn hàng
    }

    @GetMapping("/orderDetailSeller/{id}")
    public ResponseEntity<List<OrderDetail>> getAllByIdSeller(@PathVariable Integer id) {
        List<OrderDetail> orderDetails = orderDetailRepository.findAllOrderDetailByIdOrder(id);
        if (orderDetails.isEmpty()) {
            return ResponseEntity.noContent().build(); // Trả về HTTP 204 nếu không có dữ liệu
        }
        return ResponseEntity.ok(orderDetails); // Trả về danh sách chi tiết đơn hàng
    }

    // Tạo đơn hàng mới
    @PostMapping("/api/orderCreate")
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        // Tạo đối tượng đơn hàng mới từ yêu cầu
        Order order = new Order();
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(1);
        order.setUser(orderRequest.getOrder().getUser());
        order.setPaymentmethod(paymentMethod);
        order.setShippinginfor(orderRequest.getOrder().getShippinginfor());
        order.setStore(orderRequest.getOrder().getStore());
        order.setPaymentdate(orderRequest.getOrder().getPaymentdate());
        order.setOrderstatus(orderRequest.getOrder().getOrderstatus());

        // Lưu đơn hàng và lấy đối tượng đơn hàng đã lưu
        Order savedOrder = orderRepository.save(order);

        // Lưu các chi tiết đơn hàng và cập nhật số lượng sản phẩm
        if (orderRequest.getOrderDetails() != null) {
            for (OrderDetail detailRequest : orderRequest.getOrderDetails()) {
                OrderDetail detail = new OrderDetail();
                detail.setOrder(savedOrder);
                detail.setProductDetail(detailRequest.getProductDetail());
                detail.setQuantity(detailRequest.getQuantity());
                detail.setPrice(detailRequest.getPrice());
                orderDetailRepository.save(detail); // Lưu chi tiết đơn hàng vào cơ sở dữ liệu

                ProductDetail product = productDetailRepository.findById(detailRequest.getProductDetail().getId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
                if (product.getQuantity() < detailRequest.getQuantity()) {
                    return ResponseEntity.badRequest().body(null); // Hoặc xử lý trường hợp không đủ hàng tồn kho
                }
                product.setQuantity(product.getQuantity() - detailRequest.getQuantity());
                productDetailRepository.save(product); // Lưu cập nhật số lượng sản phẩm

                CartItem cartItem = cartRepository.findByProductDetailAndUser(detailRequest.getProductDetail(),
                        savedOrder.getUser());
                if (cartItem != null) {
                    cartRepository.delete(cartItem); // Xóa CartItem để tránh trùng lặp
                }
            }
        }

        return ResponseEntity.ok(savedOrder); // Trả về đối tượng đơn hàng đã lưu
    }

}