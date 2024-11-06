package com.duantn.be_project.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Repository.CartRepository;
import com.duantn.be_project.Repository.ProductDetailRepository;
import com.duantn.be_project.Repository.UserRepository;
import com.duantn.be_project.model.CartItem;
import com.duantn.be_project.model.ProductDetail;
import com.duantn.be_project.model.User;

@CrossOrigin("*")
@RestController
public class CartController {

    @Autowired
    CartRepository cartRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductDetailRepository productDetailRepository;

    // hiển danh sách productDetail bằng product id
    @GetMapping("/productDetailByProductId/{id}")
    public ResponseEntity<List<ProductDetail>> getByProductId(@PathVariable("id") Integer id) {
        List<ProductDetail> productDetails = productDetailRepository.findByIdProduct(id);

        return ResponseEntity.ok(productDetails);
    }

    @PutMapping("/cartProductDetailUpdate/{id}")
    public ResponseEntity<?> updateProductDetail(@PathVariable("id") Integer id,
            @RequestBody Map<String, Integer> payload) {
        CartItem cartItem = cartRepository.findById(id).orElse(null);

        if (cartItem == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart item not found");
        }

        Integer productDetailId = payload.get("productDetailId"); // Lấy productDetailId từ payload

        if (productDetailId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing productDetailId");
        }

        ProductDetail productDetail = productDetailRepository.findById(productDetailId).orElse(null);

        if (productDetail == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product detail not found");
        }

        cartItem.setProductDetail(productDetail);
        cartRepository.save(cartItem);

        return ResponseEntity.ok(cartItem);
    }

    @RequestMapping("/cart") 
    public ResponseEntity<?> getProductByIds(@RequestParam("id") String ids) {
        String[] cartIds = ids.split(","); 
        List<CartItem> cartItems = new ArrayList<>(); 

        for (String id : cartIds) { 
            CartItem cartItem = cartRepository.findById(Integer.parseInt(id)).orElse(null); 
            if (cartItem != null) { 
                cartItems.add(cartItem); // Thêm vào danh sách kết quả
            } else {
                return ResponseEntity.notFound().build(); // Trả về 404 nếu không tìm thấy CartItem
            }
        }
        return ResponseEntity.ok(cartItems); // Trả về danh sách CartItem tìm thấy
    }

    @PutMapping("/cartUpdate/{id}")
    public ResponseEntity<CartItem> updateQuantity(@PathVariable("id") Integer id, @RequestBody Integer quantity) {
        CartItem cartItem = cartRepository.findById(id).orElse(null);

        if (cartItem == null) {
            return ResponseEntity.notFound().build();
        }

        cartItem.setQuantity(quantity);
        cartRepository.save(cartItem);
        return ResponseEntity.ok(cartItem);
    }

    @GetMapping("/cart/{id}")
    public ResponseEntity<List<CartItem>> getById(@PathVariable("id") Integer id) {
        List<CartItem> cartItems = cartRepository.findAllCartItemlByIdUser(id);

        return ResponseEntity.ok(cartItems);
    }

    @GetMapping("/countCartIdUser/{id}")
    public ResponseEntity<List<CartItem>> getByAllCartByUserId(@PathVariable("id") Integer id) {
        List<CartItem> cartItems = cartRepository.findAllCartItemlByIdUser(id);

        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/cart/add")
    public ResponseEntity<?> addToCart(@RequestBody CartItem cartItem) {
        try {
            User user = userRepository.findById(cartItem.getUser().getId()).orElse(null);
            ProductDetail productDetail = productDetailRepository.findById(cartItem.getProductDetail().getId())
                    .orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Người dùng không tồn tại");
            }
            if (productDetail == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sản phẩm không tồn tại");
            }
            CartItem existingCartItem = cartRepository.findByUserIdAndProductDetailId(user.getId(),
                    productDetail.getId());
            if (existingCartItem != null) {
                existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItem.getQuantity());
                return ResponseEntity.ok(cartRepository.save(existingCartItem));
            } else {
                cartItem.setUser(user);
                cartItem.setProductDetail(productDetail);
                return ResponseEntity.ok(cartRepository.save(cartItem));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi server: " + e.getMessage());
        }
    }

    @DeleteMapping("/cartDelete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        cartRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cartCount/{userId}/{productDetailId}")
    public ResponseEntity<Long> getCartItemCount(@PathVariable("userId") Integer userId,
            @PathVariable("productDetailId") Integer productDetailId) {
        Long count = cartRepository.countByUserIdAndProductDetailId(userId, productDetailId);
        return ResponseEntity.ok(count);
    }

}