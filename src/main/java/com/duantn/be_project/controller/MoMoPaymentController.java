package com.duantn.be_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Repository.CartRepository;
import com.duantn.be_project.Repository.OrderDetailRepository;
import com.duantn.be_project.Repository.OrderRepository;
import com.duantn.be_project.Repository.ProductDetailRepository;
import com.duantn.be_project.Service.MoMoService;
import com.duantn.be_project.model.CartItem;
import com.duantn.be_project.model.Order;
import com.duantn.be_project.model.OrderDetail;
import com.duantn.be_project.model.PaymentMethod;
import com.duantn.be_project.model.ProductDetail;
import com.duantn.be_project.model.Request_Response.OrderRequest;

import java.security.SecureRandom;

@CrossOrigin("*")
@RestController
public class MoMoPaymentController {
    @Autowired
    private MoMoService moMoService;
    @Autowired
    ProductDetailRepository productDetailRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderDetailRepository orderDetailRepository;
    @Autowired
    CartRepository cartRepository;

    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop', 'Buyer_Manage_Buyer')")
    @GetMapping("/pay")
    public ResponseEntity<String> pay(@RequestParam Long amount, @RequestParam String ids,
            @RequestParam String address) {
        String orderId = generateRandomOrderId(10);

        String orderInfo = "Thanh toán bằng MoMo " + ids + "," + address; // Thông tin đơn hàng
        try {
            String paymentResponse = moMoService.createPayment(amount, orderId, orderInfo);
            return ResponseEntity.ok(paymentResponse); // Trả về URL thanh toán
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Đã xảy ra lỗi trong quá trình xử lý thanh toán: " + e.getMessage());
        }
    }

    // Phương thức tạo ID đơn hàng ngẫu nhiên
    private String generateRandomOrderId(int length) {
        // Chỉ cho phép ký tự chữ cái và số, và thêm dấu gạch dưới hoặc dấu chấm
        String characters = "0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder orderId = new StringBuilder(length);

        // Tạo orderId ngẫu nhiên
        for (int i = 0; i < length; i++) {
            orderId.append(characters.charAt(random.nextInt(characters.length())));
        }

        // Kiểm tra xem orderId có hợp lệ với regex không
        String regex = "^[0-9a-zA-Z]([-_.]*[0-9a-zA-Z]+)*$";
        if (!orderId.toString().matches(regex)) {
            return generateRandomOrderId(length); // Gọi lại phương thức nếu không hợp lệ
        }

        return orderId.toString();
    }

    // Tạo đơn auto ra momo
    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop', 'Buyer_Manage_Buyer')")
    @PostMapping("/createMoMoOrder")
    public ResponseEntity<Order> createOrderVnPay(@RequestBody OrderRequest orderRequest) {
        System.out.println("API called");
        Order order = new Order();
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(8);
        order.setUser(orderRequest.getOrder().getUser());
        order.setPaymentmethod(paymentMethod);
        order.setShippinginfor(orderRequest.getOrder().getShippinginfor());
        order.setStore(orderRequest.getOrder().getStore());
        order.setPaymentdate(orderRequest.getOrder().getPaymentdate());
        order.setOrderstatus(orderRequest.getOrder().getOrderstatus());

        Order savedOrder = orderRepository.save(order);

        if (orderRequest.getOrderDetails() != null) {
            for (OrderDetail detailRequest : orderRequest.getOrderDetails()) {
                OrderDetail detail = new OrderDetail();
                detail.setOrder(savedOrder);
                detail.setProductDetail(detailRequest.getProductDetail());
                detail.setQuantity(detailRequest.getQuantity());
                detail.setPrice(detailRequest.getPrice());
                orderDetailRepository.save(detail);

                ProductDetail productDetail = productDetailRepository.findById(detailRequest.getProductDetail().getId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
                if (productDetail.getQuantity() < detailRequest.getQuantity()) {
                    return ResponseEntity.badRequest().body(null);
                }
                productDetail.setQuantity(productDetail.getQuantity() - detailRequest.getQuantity());
                productDetailRepository.save(productDetail);

                CartItem cartItem = cartRepository.findByProductDetailAndUser(detailRequest.getProductDetail(),
                        savedOrder.getUser());
                if (cartItem != null) {
                    cartRepository.delete(cartItem);
                }
            }
        }
        System.out.println("Order and details saved");

        return ResponseEntity.ok(savedOrder);
    }

}