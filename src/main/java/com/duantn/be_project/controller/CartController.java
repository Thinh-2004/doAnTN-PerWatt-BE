package com.duantn.be_project.controller;

import java.util.ArrayList;
import java.util.List;

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
import com.duantn.be_project.Repository.ProductRepository;
import com.duantn.be_project.Repository.UserRepository;
import com.duantn.be_project.model.CartItem;
import com.duantn.be_project.model.Product;
import com.duantn.be_project.model.User;

@CrossOrigin("*") // Cho phép tất cả các nguồn (origin) truy cập vào API này
@RestController // Đánh dấu lớp này là một controller REST
public class CartController {

    @Autowired // Tự động tiêm CartRepository
    private CartRepository cartRepository;

    @Autowired // Tự động tiêm UserRepository
    private UserRepository userRepository;

    @Autowired // Tự động tiêm ProductRepository
    private ProductRepository productRepository;

    //Vào trang thanh toán
    @RequestMapping("/cart") // Định nghĩa endpoint GET /cart với tham số id
    public ResponseEntity<?> getProductByIds(@RequestParam("id") String ids) {
        String[] cartIds = ids.split(","); // Chia các ID từ chuỗi tham số thành mảng
        List<CartItem> cartItems = new ArrayList<>(); // Tạo danh sách để chứa các CartItem

        for (String id : cartIds) { // Duyệt qua từng ID
            CartItem cartItem = cartRepository.findById(Integer.parseInt(id)).orElse(null); // Tìm CartItem theo ID
            if (cartItem != null) { // Nếu tìm thấy CartItem
                cartItems.add(cartItem); // Thêm vào danh sách kết quả
            } else {
                return ResponseEntity.notFound().build(); // Trả về 404 nếu không tìm thấy CartItem
            }
        }
        return ResponseEntity.ok(cartItems); // Trả về danh sách CartItem tìm thấy
    }

    //cập nhật số lượng sản phẩm
    @PutMapping("/cartUpdate/{id}") // Định nghĩa endpoint PUT /cartUpdate/{id}
    public ResponseEntity<CartItem> updateQuantity(@PathVariable("id") Integer id, @RequestBody Integer quantity) {
        CartItem cartItem = cartRepository.findById(id).orElse(null); // Tìm CartItem theo ID

        if (cartItem == null) { // Nếu không tìm thấy CartItem
            return ResponseEntity.notFound().build(); // Trả về 404
        }

        cartItem.setQuantity(quantity); // Cập nhật số lượng CartItem
        cartRepository.save(cartItem); // Lưu lại CartItem đã cập nhật

        return ResponseEntity.ok(cartItem); // Trả về CartItem đã cập nhật
    }

    //hiển thị giỏ hàng của user
    @GetMapping("/cart/{id}") // Định nghĩa endpoint GET /cart/{id}
    public ResponseEntity<List<CartItem>> getById(@PathVariable("id") Integer id) {
        List<CartItem> cartItems = cartRepository.findAllCartItemlByIdUser(id); // Tìm tất cả CartItem theo ID người
                                                                                // dùng
        if (cartItems.isEmpty()) { // Nếu danh sách CartItem rỗng
            return ResponseEntity.notFound().build(); // Trả về 404
        }
        return ResponseEntity.ok(cartItems); // Trả về danh sách CartItem
    }

    //điếm số lượng giỏ hàng
    @GetMapping("/countCartIdUser/{id}") // Định nghĩa endpoint GET /countCartIdUser/{id}
    public ResponseEntity<List<CartItem>> getByAllCartByUserId(@PathVariable("id") Integer id) {
        List<CartItem> cartItems = cartRepository.findAllCartItemlByIdUser(id); // Tìm tất cả CartItem theo ID người
                                                                                // dùng
        if (cartItems.isEmpty()) { // Nếu danh sách CartItem rỗng
            return ResponseEntity.notFound().build(); // Trả về 404
        }
        return ResponseEntity.ok(cartItems); // Trả về danh sách CartItem
    }

    //thêm sản phẩm vào giỏ hàng
    @PostMapping("/cart/add") // Định nghĩa endpoint POST /cart/add
    public ResponseEntity<CartItem> addToCart(@RequestBody CartItem cartItem) {
        try {
            User user = userRepository.findById(cartItem.getUser().getId()).orElse(null); // Tìm người dùng theo ID
            Product product = productRepository.findById(cartItem.getProduct().getId()).orElse(null); // Tìm sản phẩm
                                                                                                      // theo ID

            if (user == null || product == null) { // Nếu người dùng hoặc sản phẩm không tồn tại
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Trả về lỗi 400
            }

            CartItem existingCartItem = cartRepository.findByUserIdAndProductId(user.getId(), product.getId()); // Tìm
                                                                                                                // CartItem
                                                                                                                // hiện
                                                                                                                // có
                                                                                                                // theo
                                                                                                                // userId
                                                                                                                // và
                                                                                                                // productId

            if (existingCartItem != null) { // Nếu CartItem đã tồn tại
                existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItem.getQuantity()); // Cập nhật số
                                                                                                       // lượng
                return ResponseEntity.ok(cartRepository.save(existingCartItem)); // Lưu lại và trả về CartItem đã cập
                                                                                 // nhật
            } else {
                cartItem.setUser(user); // Gán người dùng cho CartItem mới
                cartItem.setProduct(product); // Gán sản phẩm cho CartItem mới
                return ResponseEntity.ok(cartRepository.save(cartItem)); // Lưu lại và trả về CartItem mới
            }
        } catch (Exception e) { // Xử lý ngoại lệ nếu có lỗi xảy ra
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Trả về lỗi 500
        }
    }


    //xóa sản phẩm ra khỏi giỏ hàng
    @DeleteMapping("/cartDelete/{id}") // Định nghĩa endpoint DELETE /cartDelete/{id}
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        if (!cartRepository.existsById(id)) { // Kiểm tra nếu CartItem không tồn tại
            return ResponseEntity.notFound().build(); // Trả về 404
        }
        cartRepository.deleteById(id); // Xóa CartItem theo ID
        return ResponseEntity.ok().build(); // Trả về 200 OK
    }
}
