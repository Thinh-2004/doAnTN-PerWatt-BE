package com.duantn.be_project.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.duantn.be_project.Repository.RoleRepository;
import com.duantn.be_project.Repository.UserRepository;
import com.duantn.be_project.Service.SecurityConfig;
import com.duantn.be_project.Service.UserService;
import com.duantn.be_project.model.Role;
import com.duantn.be_project.model.User;
import com.duantn.be_project.untils.UploadImages;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@CrossOrigin("*")
@RestController
public class UserController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UploadImages uploadImages;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserService userService;

    // GetAll
    @GetMapping("/user")
    public ResponseEntity<List<User>> getAll(Model model) {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // GetByIdUser
    @GetMapping("/userProFile/{id}")
    public ResponseEntity<User> getByIdUser(@PathVariable("id") Integer id) {
        User user = userRepository.findById(id).orElseThrow();
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    // checkmkProfileUser
    @PostMapping("/checkPass")
    public ResponseEntity<Map<String, Object>> checkPass(@RequestBody User userRequest) {
        Map<String, Object> response = new HashMap<>();

        // Tìm kiếm người dùng theo Email
        User user = userRepository.findById(userRequest.getId()).orElseThrow();
        if (user == null) {
            response.put("message", "Người dùng không tồn tại");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Kiểm tra mật khẩu
        if (passwordEncoder.matches(userRequest.getPassword(), user.getPassword())) {
            response.put("message", "Mật khẩu chính xác");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Mật khẩu không chính xác");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User userRequest) {
        Map<String, Object> response = new HashMap<>();
        User user = userRepository.findByEmail(userRequest.getEmail());
        if (user == null) {
            response.put("message", "Email không tồn tại");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if (passwordEncoder.matches(userRequest.getPassword(), user.getPassword())) {
            response.put("user", user);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Mật khẩu không chính xác");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/user")
    public ResponseEntity<?> post(@RequestBody User user) {
        // Kiểm tra email đã tồn tại
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email đã tồn tại");
        }

        // Tìm role
        Role role = roleRepository.findById(user.getRole().getId())
                .orElseThrow(() -> new RuntimeException("Quyền không tồn tại"));
        user.setRole(role);

        // Mã hóa mật khẩu trước khi lưu
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // Lưu user để lấy ID
        User savedUser = userRepository.save(user);

        // Lưu ảnh đại diện dựa trên giới tính
        String avatarFilename = uploadImages.saveUserImageBasedOnGender(user.getGender(), savedUser.getId());
        if (avatarFilename == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lưu trữ ảnh đại diện");
        }

        savedUser.setAvatar(avatarFilename);
        userRepository.save(savedUser);

        return ResponseEntity.ok(savedUser);
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable("id") Integer id,
            @RequestPart("user") String userJson,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {

        // Kiểm tra xem người dùng có tồn tại không
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Chuyển đổi
        ObjectMapper objectMapper = new ObjectMapper();
        User user;
        try {
            user = objectMapper.readValue(userJson, User.class);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Dữ liệu người dùng không hợp lệ: " + e.getMessage());
        }

        // Đảm bảo ID của người dùng khớp với biến đường dẫn
        user.setId(id);
        User checkUser = userRepository.findById(id).orElseThrow();
        if (checkUser.getPassword().equalsIgnoreCase(user.getPassword())) {
            user.setPassword(user.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        String oldAvatarUrl = null;

        // Xử lý hình ảnh nếu có
        if (avatar != null && !avatar.isEmpty()) {
            // Lưu thông tin ảnh cũ nếu có
            oldAvatarUrl = userRepository.findById(id)
                    .map(User::getAvatar)
                    .orElse(null);

            try {
                // Lưu hình ảnh mới và lấy URL hoặc tên tệp
                String newAvatarUrl = uploadImages.saveUserImage(avatar, id);
                user.setAvatar(newAvatarUrl);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Không thể lưu hình ảnh: " + e.getMessage());
            }
        } else {
            String setOldAvatarUrl = userRepository.findById(id)
                    .map(User::getAvatar)
                    .orElse(null);
            user.setAvatar(setOldAvatarUrl);
        }

        // Lưu người dùng đã cập nhật
        try {
            User updatedUser = userRepository.save(user);

            // Xóa ảnh cũ nếu có
            if (oldAvatarUrl != null) {
                String filePath = String.format("src/main/resources/static/files/user/%d/%s", id, oldAvatarUrl);
                File file = new File(filePath);
                if (file.exists()) {
                    if (file.delete()) {
                        System.out.println("Đã xóa ảnh cũ: " + filePath);
                    } else {
                        System.out.println("Không thể xóa ảnh cũ: " + filePath);
                    }
                } else {
                    System.out.println("Ảnh cũ không tồn tại: " + filePath);
                }
            }

            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Không thể cập nhật người dùng: " + e.getMessage());
        }
    }

    // delete
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    ////////
    @GetMapping("/info/{id}")
    public ResponseEntity<User> getUserInfo(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/info/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }
}
