package com.duantn.be_project.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import com.duantn.be_project.Repository.CartRepository;
import com.duantn.be_project.Repository.ProductRepository;
import com.duantn.be_project.Repository.UserRepository;
import com.duantn.be_project.model.CartItem;
import com.duantn.be_project.model.Product;
import com.duantn.be_project.model.User;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin("*")
@RestController
public class CartController {
    @Autowired
    CartRepository cartRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductRepository productRepository;

    @RequestMapping("/cart")
    public ResponseEntity<?> getProductByIds(@RequestParam("id") String ids) {
        String[] cartIds = ids.split(",");
        List<CartItem> cartItems = new ArrayList<>();

        for (String id : cartIds) {
            CartItem cartItem = cartRepository.findById(Integer.parseInt(id)).orElse(null);
            if (cartItem != null) {
                cartItems.add(cartItem);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.ok(cartItems);
    }

    @PutMapping("/cartUpdate/{id}")
    public ResponseEntity<CartItem> updateQuantity(@PathVariable("id") Integer id, @RequestBody Integer quantity) {
        if (!cartRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Tìm đối tượng CartItem hiện tại
        CartItem cartItem = cartRepository.findById(id).orElse(null);
        if (cartItem != null) {
            // Cập nhật số lượng
            cartItem.setQuantity(quantity);
            // Lưu đối tượng CartItem đã được cập nhật
            cartRepository.save(cartItem);
        }

        return ResponseEntity.ok(cartItem);
    }

    // // GetAll
    // @GetMapping("/cart")
    // public ResponseEntity<List<CartItem>> getAll(Model model) {
    // return ResponseEntity.ok(cartRepository.findAll());
    // }

    // GetAllById
    @GetMapping("/cart/{id}")
    public ResponseEntity<List<CartItem>> getById(@PathVariable("id") Integer id) {
List<CartItem> cartItems = cartRepository.findAllCartItemlByIdUser(id);
        if (cartRepository == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cartItems);
    }

    // GetAllById
    @GetMapping("/countCartIdUser/{id}")
    public ResponseEntity<List<CartItem>> getByAllCartByUserId(@PathVariable("id") Integer id) {
        List<CartItem> cartItems = cartRepository.findAllCartItemlByIdUser(id);
        if (cartRepository == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/cart/add")
    public ResponseEntity<CartItem> addToCart(@RequestBody CartItem cartItem) {
        try {
            User user = userRepository.findById(cartItem.getUser().getId()).orElse(null);
            Product product = productRepository.findById(cartItem.getProduct().getId()).orElse(null);

            if (user == null || product == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            // Tìm kiếm cart item đã tồn tại với userId và productId
            CartItem existingCartItem = cartRepository.findByUserIdAndProductId(user.getId(), product.getId());

            if (existingCartItem != null) {
                // Cập nhật số lượng
                existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItem.getQuantity());
                CartItem updatedCartItem = cartRepository.save(existingCartItem);
                return ResponseEntity.ok(updatedCartItem);
            } else {
                // Thêm mới cart item
                cartItem.setUser(user);
                cartItem.setProduct(product);
                CartItem savedCartItem = cartRepository.save(cartItem);
                return ResponseEntity.ok(savedCartItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Delete
    @DeleteMapping("/cartDelete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        // TODO: process PUT request
        if (!cartRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        cartRepository.deleteById(id);
        return ResponseEntity.ok().build();
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