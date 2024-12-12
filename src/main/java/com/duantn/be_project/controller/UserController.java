package com.duantn.be_project.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.duantn.be_project.Repository.RolePermissionReponsitory;

import com.duantn.be_project.Repository.UserRepository;
import com.duantn.be_project.Service.FirebaseStorageService;
import com.duantn.be_project.Service.UserService;

import com.duantn.be_project.model.RolePermission;
import com.duantn.be_project.model.User;
import com.duantn.be_project.model.Request_Response.TokenRequest;
import com.duantn.be_project.security.model.ApiResponse;
import com.duantn.be_project.security.model.AuthenticateRequest;
import com.duantn.be_project.security.model.AuthenticationResponse;
import com.duantn.be_project.security.service.authenticateService;
import com.duantn.be_project.security.service.jwtService;
import com.duantn.be_project.untils.UploadImages;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.nimbusds.jose.JOSEException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@CrossOrigin("*")
@RestController
public class UserController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RolePermissionReponsitory rolePermissionReponsitory;
    @Autowired
    UploadImages uploadImages;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserService userService;
    @Autowired
    authenticateService authenticateService;
    @Autowired
    jwtService jwtService;
    @Autowired
    FirebaseStorageService firebaseStorageService;

    // GetAll
    @PreAuthorize("hasAnyAuthority('Admin_All_Function', 'Admin_Manage_Support')")
    @GetMapping("/user")
    public ResponseEntity<?> getAllUser(
            @RequestParam("pageNo") Optional<Integer> pageNo,
            @RequestParam("pageSize") Optional<Integer> pageSize,
            @RequestParam(name = "keyWord", defaultValue = "") String keyWord,
            @RequestParam(name = "checkFilter", defaultValue = "") String checkFilter,
            @RequestParam(name = "sortBy", defaultValue = "") String sortBy) {

        // Thiết lập sort
        Sort sort = Sort.by(Direction.DESC, "u.id");
        switch (sortBy) {
            case "DESCName":
                sort = Sort.by(Direction.DESC, "u.id");
                break;
            case "ASCName":
                sort = Sort.by(Direction.ASC, "u.id");
                break;
            default:
                sort = Sort.by(Direction.DESC, "u.id");
                break;
        }
        // Thiết lập trang
        Pageable pageable = PageRequest.of(pageNo.orElse(0), pageSize.orElse(10), sort);
        // Thiết lập dữ liệu hiển thị theo trang
        Page<User> uPage = null;
        // Kiểm tra nếu keyWord trống
        if (!keyWord.isEmpty()) {
            // Kiểm tra nếu keyWord là email
            String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
            if (keyWord.matches(emailRegex)) {
                uPage = userRepository.listUser("%" + keyWord + "%", "%", "%", pageable);
            } else {
                uPage = userRepository.listUser("%", "%" + keyWord + "%", "%", pageable);
            }

        } else if (!checkFilter.isEmpty()) {
            if (checkFilter.equals("Buyer")) {
                uPage = userRepository.listUser("%", "%", "%" + checkFilter + "%", pageable);
            } else if (checkFilter.equals("Seller")) {
                uPage = userRepository.listUser("%", "%", "%" + checkFilter + "%", pageable);
            }
        } else {
            uPage = userRepository.listUser("%", "%", "%", pageable);
        }

        // Tạo Map để trả dữ liệu
        Map<String, Object> response = new HashMap<>();
        response.put("users", uPage.getContent()); // Danh sách sản phẩm
        response.put("currentPage", uPage.getNumber()); // Trang hiện tại (hiển
        // thị từ 1)
        response.put("totalPages", uPage.getTotalPages()); // Tổng số trang
        response.put("totalItems", uPage.getTotalElements()); // Tổng số sản phẩm
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('Admin_All_Function', 'Admin_Manage_Support')")
    @GetMapping("/user/admin")
    public ResponseEntity<?> getAllUserAdmin(
            @RequestParam("pageNo") Optional<Integer> pageNo,
            @RequestParam("pageSize") Optional<Integer> pageSize,
            @RequestParam(name = "keyWord", defaultValue = "") String keyWord,
            @RequestParam(name = "checkSelected", required = false) Integer checkSelected,
            @RequestParam(name = "sortBy", defaultValue = "") String sortBy) {

        Sort sort;
        switch (sortBy) {
            case "DESCName":
                sort = Sort.by(Direction.DESC, "u.id");
                break;
            case "ASCName":
                sort = Sort.by(Direction.ASC, "u.id");
                break;
            default:
                sort = Sort.by(Direction.DESC, "u.id");
                break;
        }

        Pageable pageable = PageRequest.of(pageNo.orElse(0), pageSize.orElse(10), sort);
        Page<User> uPage;

        if (!keyWord.isEmpty()) {
            String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
            if (keyWord.matches(emailRegex)) {
                uPage = userRepository.listUserAdmin("%" + keyWord + "%", "%", pageable);
            } else {
                uPage = userRepository.listUserAdmin("%", "%" + keyWord + "%", pageable);
            }
        } else if (checkSelected != null) {
            uPage = userRepository.listUserAdminByIdPermission(checkSelected, pageable);
        } else {
            uPage = userRepository.listUserAdmin("%", "%", pageable);
        }

        Map<String, Object> response = Map.of(
                "users", uPage.getContent(),
                "currentPage", uPage.getNumber(),
                "totalPages", uPage.getTotalPages(),
                "totalItems", uPage.getTotalElements());
        return ResponseEntity.ok(response);
    }

    // GetByIdUser
    // Chỉ được gọi khi email trùng với email trong token
    @PostAuthorize("returnObject.body.email == authentication.name")
    @GetMapping("/userProFile/myInfo")
    public ResponseEntity<User> getMyInfo() {
        String emailUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(emailUser);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getAuthorities());
        return ResponseEntity.ok(user);
    }

    // GetByIdUser
    // Chỉ được gọi khi email trùng với email trong token
    @PreAuthorize("hasAnyAuthority('Admin_All_Function', 'Admin_Manage_Support')")
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

    @PostMapping("/form/login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticateRequest request) {

        AuthenticationResponse result = authenticateService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    // @PreAuthorize("hasAnyAuthority('Seller', 'Buyer', 'Admin')")
    @PostMapping("/form/refesh")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody Map<String, String> token)
            throws ParseException, JOSEException {
        AuthenticationResponse result = jwtService.refeshToken(token.get("token"));
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop', 'Buyer_Manage_Buyer', 'Admin_All_Function', 'Admin_Manage_Category', 'Admin_Manage_Banner', 'Admin_Manage_Revenue', 'Admin_Manage_Support','Admin_Manage_Promotion')")
    @PostMapping("/form/logout")
    public ApiResponse<Void> logout(@RequestBody Map<String, String> requestBody) throws ParseException, JOSEException {
        String token = requestBody.get("token"); // Lấy token từ body
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token không được để trống");
        }

        // Kiểm tra và xử lý token
        authenticateService.Logout(token);

        // Trả về phản hồi thành công
        return ApiResponse.<Void>builder()
                .message("Đăng xuất thành công")
                .build();
    }

    @PreAuthorize("hasAnyAuthority('Admin_All_Function')")
    @PostMapping("/manage/create")
    public ResponseEntity<?> postAdminManage(@RequestBody User user) {

        // Kiểm tra email đã tồn tại
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email đã tồn tại");
        }

        // Tìm role
        RolePermission rolePermission = rolePermissionReponsitory.findById(user.getRolePermission().getId())
                .orElseThrow(() -> new RuntimeException("Quyền không tồn tại"));
        user.setRolePermission(rolePermission);

        // Mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // Lưu user
        User savedUser = userRepository.save(user);

        // Lấy ảnh mặc định theo giới tính
        String defaultAvatar = user.getGender() ? "nam.jpg" : "nu.jpg";
        Path sourcePath = Paths.get("src/main/resources/static/files/images", defaultAvatar);

        try {
            // Đọc file
            byte[] fileContent = Files.readAllBytes(sourcePath);

            // Upload lên Firebase
            String avatarUserUrl = firebaseStorageService.uploadToFirebaseByUserGender(fileContent,
                    "users/" + savedUser.getId() + ".jpg");

            if (avatarUserUrl == null) {
                userRepository.deleteById(savedUser.getId());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi upload ảnh lên Firebase");
            }

            // Cập nhật avatar
            savedUser.setAvatar(avatarUserUrl);
            userRepository.save(savedUser);

            return ResponseEntity.ok(savedUser);
        } catch (IOException e) {
            userRepository.deleteById(savedUser.getId());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể xử lý ảnh đại diện");
        }
    }

    @PreAuthorize("hasAnyAuthority('Admin_All_Function')")
    @PutMapping("/manage/update/{id}")
    public ResponseEntity<?> updateUserAdmin(
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
        User existingUser = userRepository.findById(user.getId()).orElseThrow();

        // Kiểm tra nếu mật khẩu mới không null hoặc không rỗng
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            // Kiểm tra xem mật khẩu mới có trùng với mật khẩu hiện tại hay không
            if (!user.getPassword().equalsIgnoreCase(existingUser.getPassword())) {
                // Nếu mật khẩu không trùng, mã hóa mật khẩu mới
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                // Nếu mật khẩu trùng, giữ nguyên mật khẩu hiện tại
                user.setPassword(existingUser.getPassword());
            }
        } else {
            // Nếu mật khẩu mới là null hoặc rỗng, giữ nguyên mật khẩu hiện tại
            user.setPassword(existingUser.getPassword());
        }

        // Lưu tên ảnh cũ
        String oldImageDetail = existingUser.getAvatar();
        // Giải mã URL trước
        String decodedUrl = java.net.URLDecoder.decode(oldImageDetail,
                java.nio.charset.StandardCharsets.UTF_8);

        // Loại bỏ phần https://firebasestorage.googleapis.com/v0/b/ và lấy phần sau o/
        String filePath = decodedUrl.split("o/")[1]; // Tách phần sau "o/"

        // Loại bỏ phần ?alt=media
        int queryIndex = filePath.indexOf("?"); // Tìm vị trí của dấu hỏi "?"
        if (queryIndex != -1) {
            filePath = filePath.substring(0, queryIndex); // Cắt bỏ phần sau dấu hỏi
        }
        // Xử lý hình ảnh nếu có
        if (avatar != null && !avatar.isEmpty()) {
            try {
                // Lưu hình ảnh mới lên Firebase và lấy URL
                String newAvatarUrl = firebaseStorageService.uploadToFirebase(avatar,
                        "users");

                // Xóa ảnh cũ trên Firebase nếu có
                if (oldImageDetail != null && !oldImageDetail.isEmpty()) {
                    try {
                        firebaseStorageService.deleteFileFromFirebase(filePath);
                    } catch (Exception e) {
                        System.err.println("Không thể xóa ảnh cũ trên Firebase: " + e.getMessage());
                    }
                }

                // Cập nhật avatar mới
                user.setAvatar(newAvatarUrl);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Không thể lưu hình ảnh: " + e.getMessage());
            }
        } else {
            // Nếu không có hình ảnh mới, giữ nguyên avatar cũ
            user.setAvatar(existingUser.getAvatar());
        }

        // Lưu người dùng đã cập nhật
        try {
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Không thể cập nhật người dùng: " + e.getMessage());
        }
    }

    @PostMapping("/user")
    public ResponseEntity<?> post(@RequestBody User user) {

        // Kiểm tra email đã tồn tại
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email đã tồn tại");
        }

        // Tìm role
        RolePermission rolePermission = rolePermissionReponsitory.findById(user.getRolePermission().getId())
                .orElseThrow(() -> new RuntimeException("Quyền không tồn tại"));
        user.setRolePermission(rolePermission);

        // Mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // Lưu user
        User savedUser = userRepository.save(user);

        // Lấy ảnh mặc định theo giới tính
        String defaultAvatar = user.getGender() ? "nam.jpg" : "nu.jpg";
        Path sourcePath = Paths.get("src/main/resources/static/files/images", defaultAvatar);

        try {
            // Đọc file
            byte[] fileContent = Files.readAllBytes(sourcePath);

            // Upload lên Firebase
            String avatarUserUrl = firebaseStorageService.uploadToFirebaseByUserGender(fileContent,
                    "users/" + savedUser.getId() + ".jpg");

            if (avatarUserUrl == null) {
                userRepository.deleteById(savedUser.getId());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi upload ảnh lên Firebase");
            }

            // Cập nhật avatar
            savedUser.setAvatar(avatarUserUrl);
            userRepository.save(savedUser);

            return ResponseEntity.ok(savedUser);
        } catch (IOException e) {
            userRepository.deleteById(savedUser.getId());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể xử lý ảnh đại diện");
        }
    }

    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop', 'Buyer_Manage_Buyer', 'Admin_All_Function', 'Admin_Manage_Category', 'Admin_Manage_Banner', 'Admin_Manage_Revenue', 'Admin_Manage_Support','Admin_Manage_Promotion')")
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
        User existingUser = userRepository.findById(user.getId()).orElseThrow();

        // Kiểm tra nếu mật khẩu mới không null hoặc không rỗng
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            // Kiểm tra xem mật khẩu mới có trùng với mật khẩu hiện tại hay không
            if (!user.getPassword().equalsIgnoreCase(existingUser.getPassword())) {
                // Nếu mật khẩu không trùng, mã hóa mật khẩu mới
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                // Nếu mật khẩu trùng, giữ nguyên mật khẩu hiện tại
                user.setPassword(existingUser.getPassword());
            }
        } else {
            // Nếu mật khẩu mới là null hoặc rỗng, giữ nguyên mật khẩu hiện tại
            user.setPassword(existingUser.getPassword());
        }

        // Xử lý hình ảnh nếu có
        if (avatar != null && !avatar.isEmpty()) {
            // Lưu tên ảnh cũ
            String oldImageDetail = existingUser.getAvatar();
            // Giải mã URL trước
            String decodedUrl = java.net.URLDecoder.decode(oldImageDetail,
                    java.nio.charset.StandardCharsets.UTF_8);

            // Loại bỏ phần https://firebasestorage.googleapis.com/v0/b/ và lấy phần sau o/
            String filePath = decodedUrl.split("o/")[1]; // Tách phần sau "o/"

            // Loại bỏ phần ?alt=media
            int queryIndex = filePath.indexOf("?"); // Tìm vị trí của dấu hỏi "?"
            if (queryIndex != -1) {
                filePath = filePath.substring(0, queryIndex); // Cắt bỏ phần sau dấu hỏi
            }
            try {
                // Lưu hình ảnh mới lên Firebase và lấy URL
                String newAvatarUrl = firebaseStorageService.uploadToFirebase(avatar,
                        "users");

                // Xóa ảnh cũ trên Firebase nếu có
                if (oldImageDetail != null && !oldImageDetail.isEmpty()) {
                    try {
                        firebaseStorageService.deleteFileFromFirebase(filePath);
                    } catch (Exception e) {
                        System.err.println("Không thể xóa ảnh cũ trên Firebase: " + e.getMessage());
                    }
                }

                // Cập nhật avatar mới
                user.setAvatar(newAvatarUrl);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Không thể lưu hình ảnh: " + e.getMessage());
            }
        } else {
            // Nếu không có hình ảnh mới, giữ nguyên avatar cũ
            user.setAvatar(existingUser.getAvatar());
        }

        // Lưu người dùng đã cập nhật
        try {
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Không thể cập nhật người dùng: " + e.getMessage());
        }
    }

    // delete
    @PreAuthorize("hasAnyAuthority('Admin_All_Function')")
    @DeleteMapping("/manage/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
        // Tìm hình ảnh trong cơ sở dữ liệu
        User user = userRepository.findById(id).orElse(null);
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        // Xóa hình ảnh khỏi Firebase Storage
        try {
            // Giải mã URL trước
            String decodedUrl = java.net.URLDecoder.decode(user.getAvatar(),
                    java.nio.charset.StandardCharsets.UTF_8);

            // Loại bỏ phần https://firebasestorage.googleapis.com/v0/b/ và lấy phần sau o/
            String filePath = decodedUrl.split("o/")[1]; // Tách phần sau "o/"

            // Loại bỏ phần ?alt=media
            int queryIndex = filePath.indexOf("?"); // Tìm vị trí của dấu hỏi "?"
            if (queryIndex != -1) {
                filePath = filePath.substring(0, queryIndex); // Cắt bỏ phần sau dấu hỏi
            }

            // Gọi Firebase Storage Service để xóa file
            firebaseStorageService.deleteFileFromFirebase(filePath); // Xóa file khỏi Firebase
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Không thể xóa hình ảnh khỏi Firebase: " + e.getMessage());
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Login by google
    @PostMapping("/loginByGoogle")
    public ApiResponse<AuthenticationResponse> googleLogin(@RequestBody TokenRequest tokenRequest) {
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
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setId(6);

                    user.setEmail(email);
                    user.setFullname(name);
                    user.setRolePermission(rolePermission); // Đặt chức vụ là Buyer khi được tạo tài khoản
                    user.setAvatar(pictureUrl);

                    // Lưu user tạm để lấy id
                    // User saveBeta = userRepository.save(user);

                    // // Tải ảnh từ URL và lưu vào server
                    // String avatarFilename = uploadImages.saveImageUserByLoginGoogle(pictureUrl,
                    // saveBeta.getId());
                    // if (avatarFilename == null) {
                    // throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    // "Lỗi khi lưu trữ ảnh đại diện");
                    // }

                } else {
                    user.setFullname(name);
                    user.setEmail(email);
                    userRepository.save(user);
                }

                // Trả về thông tin người dùng
                User savedUser = userRepository.save(user);
                // Khởi tạo AuthenticateRequest để chưa giá trị
                AuthenticateRequest authenticateRequest = new AuthenticateRequest(savedUser.getEmail(),
                        savedUser.getPassword(), true);
                AuthenticationResponse result = authenticateService.authenticate(authenticateRequest);
                return ApiResponse.<AuthenticationResponse>builder()
                        .result(result)
                        .build();
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid ID token.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error while verifying token: " + e.getMessage());
        }
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
