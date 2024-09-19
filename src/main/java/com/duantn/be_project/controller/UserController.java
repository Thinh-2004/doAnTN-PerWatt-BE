package com.duantn.be_project.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.duantn.be_project.Repository.RoleRepository;
import com.duantn.be_project.Repository.UserRepository;
import com.duantn.be_project.Service.UserService;
import com.duantn.be_project.Service.Security.Encryption;
import com.duantn.be_project.model.Role;
import com.duantn.be_project.model.User;
import com.duantn.be_project.model.Request.TokenRequest;
import com.duantn.be_project.untils.UploadImages;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @Autowired
    Encryption encryption;

    // @Autowired
    // private JwtDecoder jwtDecoder;

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
        // Kiểm tra nếu mật khẩu của người dùng mới là null hoặc rỗng
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(null); // Không thay đổi mật khẩu
        } else {
            // Nếu mật khẩu không trống, so sánh mật khẩu đã mã hóa
            if (passwordEncoder.matches(user.getPassword(), checkUser.getPassword())) {
                // Nếu mật khẩu trùng khớp, giữ nguyên mật khẩu
                user.setPassword(checkUser.getPassword());
            } else {
                // Nếu mật khẩu không trùng, mã hóa mật khẩu mới
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
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

    // Login by google
    @PostMapping("/loginByGoogle")
    public ResponseEntity<?> googleLogin(@RequestBody TokenRequest tokenRequest) {
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), jsonFactory)
                    .setAudience(Collections
                            .singletonList("175283151902-4ncr5sj18h9e9akpj72mmnjbcq1mqdkg.apps.googleusercontent.com"))
                    .build();

            GoogleIdToken idToken = verifier.verify(tokenRequest.getToken());

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");

                // Kiểm tra xem người dùng đã tồn tại trong cơ sở dữ liệu chưa
                User user = userRepository.findByEmail(email);
                if (user == null) {
                    // Nếu người dùng chưa tồn tại, tạo mới và lưu vào cơ sở dữ liệu
                    user = new User();
                    Role role = new Role();
                    role.setId(3);
                    user.setEmail(email);
                    user.setFullname(name);
                    user.setRole(role); // Đặt chức vụ là Buyer khi được tạo tài khoản

                    // Lưu user tạm để lấy id
                    User saveBeta = userRepository.save(user);

                    // Tải ảnh từ URL và lưu vào server
                    String avatarFilename = uploadImages.saveImageUserByLoginGoogle(pictureUrl, saveBeta.getId());
                    if (avatarFilename == null) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Lỗi khi lưu trữ ảnh đại diện");
                    }
                    user.setAvatar(avatarFilename);
                } else {
                    user.setFullname(name);
                    user.setEmail(email);
                    userRepository.save(user);
                }

                User savedUser = userRepository.save(user);

                // String jsonData = new ObjectMapper().writeValueAsString(savedUser); // Chuyển đổi đối tượng thành Json
                // String encrytedData = encryption.encrypt(jsonData);
                // Trả về thông tin người dùng
                return ResponseEntity.ok(savedUser);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid ID token.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while verifying token: " + e.getMessage());
        }
    }

}
