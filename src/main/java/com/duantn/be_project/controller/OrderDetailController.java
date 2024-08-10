package com.duantn.be_project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.duantn.be_project.Repository.CartRepository;
import com.duantn.be_project.Repository.OrderDetailRepository;
import com.duantn.be_project.Repository.OrderRepository;
import com.duantn.be_project.Repository.ProductRepository;
import com.duantn.be_project.model.CartItem;
import com.duantn.be_project.model.Order;
import com.duantn.be_project.model.OrderDetail;
import com.duantn.be_project.model.OrderRequest;
import com.duantn.be_project.model.Product;

@CrossOrigin("*") // Cho phép tất cả các nguồn truy cập vào API
@RestController
public class OrderDetailController {

    @Autowired
    OrderDetailRepository orderDetailRepository; // Inject repository để làm việc với OrderDetail
    @Autowired
    OrderRepository orderRepository; // Inject repository để làm việc với Order
    @Autowired
    CartRepository cartRepository; // Inject repository để làm việc với CartItem
    @Autowired
    ProductRepository productRepository;

    // Lấy tất cả các chi tiết đơn hàng
    @GetMapping("/orderDetail")
    public ResponseEntity<List<OrderDetail>> getAll() {
        return ResponseEntity.ok(orderDetailRepository.findAll());
    }

    // Lấy tất cả các chi tiết đơn hàng theo id đơn hàng
    @GetMapping("/orderDetail/{id}")
    public ResponseEntity<List<OrderDetail>> getAllById(@PathVariable Integer id) {
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
    @PostMapping("/orderCreate")
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        // Tạo đối tượng đơn hàng mới từ yêu cầu
        Order order = new Order();
        order.setUser(orderRequest.getOrder().getUser());
        order.setPaymentmethod(orderRequest.getOrder().getPaymentmethod());
        order.setShippinginfor(orderRequest.getOrder().getShippinginfor());
order.setFee(orderRequest.getOrder().getFee());
        order.setStore(orderRequest.getOrder().getStore());
        order.setPaymentdate(orderRequest.getOrder().getPaymentdate());
        order.setOrderstatus(orderRequest.getOrder().getOrderstatus());

        // Lưu đơn hàng và lấy đối tượng đơn hàng đã lưu
        Order savedOrder = orderRepository.save(order);

        // Lưu các chi tiết đơn hàng và cập nhật số lượng sản phẩm
        if (orderRequest.getOrderDetails() != null) {
            for (OrderDetail detailRequest : orderRequest.getOrderDetails()) {
                // Lưu chi tiết đơn hàng
                OrderDetail detail = new OrderDetail();
                detail.setOrder(savedOrder); // Liên kết chi tiết đơn hàng với đơn hàng đã lưu
                detail.setProduct(detailRequest.getProduct());
                detail.setQuantity(detailRequest.getQuantity());
                detail.setPrice(detailRequest.getPrice());
                orderDetailRepository.save(detail); // Lưu chi tiết đơn hàng vào cơ sở dữ liệu

                // Cập nhật số lượng sản phẩm
                Product product = productRepository.findById(detailRequest.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));
                if (product.getQuantity() < detailRequest.getQuantity()) {
                    return ResponseEntity.badRequest().body(null); // Hoặc xử lý trường hợp không đủ hàng tồn kho
                }
                product.setQuantity(product.getQuantity() - detailRequest.getQuantity());
                productRepository.save(product); // Lưu cập nhật số lượng sản phẩm

                // Xóa CartItem tương ứng với sản phẩm đã đặt
                CartItem cartItem = cartRepository.findByProductAndUser(detailRequest.getProduct(),
                        savedOrder.getUser());
                if (cartItem != null) {
                    cartRepository.delete(cartItem); // Xóa CartItem để tránh trùng lặp
                }
            }
        }

        return ResponseEntity.ok(savedOrder); // Trả về đối tượng đơn hàng đã lưu
    }

}