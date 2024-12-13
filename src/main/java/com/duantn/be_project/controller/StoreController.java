package com.duantn.be_project.controller;

import java.io.File;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.duantn.be_project.Repository.CategoryRepository;
import com.duantn.be_project.Repository.RolePermissionReponsitory;

import com.duantn.be_project.Repository.StoreRepository;
import com.duantn.be_project.Repository.UserRepository;
import com.duantn.be_project.Service.FirebaseStorageService;
import com.duantn.be_project.Service.SlugText.SlugText;
import com.duantn.be_project.model.ProductCategory;
import com.duantn.be_project.model.RolePermission;
import com.duantn.be_project.model.Store;
import com.duantn.be_project.model.User;
import com.duantn.be_project.untils.UploadImages;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@CrossOrigin("*")
@RestController
public class StoreController {
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RolePermissionReponsitory rolePermissionReponsitory;
    @Autowired
    UploadImages uploadImages;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    SlugText slugText;
    @Autowired
    FirebaseStorageService firebaseStorageService;

    // Get All
    @GetMapping("/store")
    public ResponseEntity<List<Store>> getAll(Model model) {
        return ResponseEntity.ok(storeRepository.findAll());
    }

    // Get All
    @GetMapping("/store/{id}")
    public ResponseEntity<Store> getStoreById(@PathVariable("id") Integer storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow();
        if (store == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(store);
    }

    @GetMapping("/store/checkIdUser/{id}")
    public ResponseEntity<Map<String, Boolean>> checkStoreByUserId(@PathVariable("id") Integer userId) {
        boolean exists = storeRepository.findStoreByIdUser(userId) != null;
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    // Post
    @PreAuthorize("hasAnyAuthority('Buyer_Manage_Buyer')")
    @PostMapping("/store")
    public ResponseEntity<?> post(@RequestBody Store store) {
        // Bắt lỗi
        ResponseEntity<String> validateRes = validate(store);
        if (validateRes != null) {
            return validateRes;
        }

        if (storeRepository.existsByNamestoreIgnoreCase(store.getNamestore())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Tên cửa hàng đã tồn tại!");
        } else if (!store.getTaxcode().isEmpty()) {
            Integer countTaxCode = storeRepository.checkDuplicate(store.getTaxcode());
            if (countTaxCode > 0) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Mã số thuế không tồn tại hoặc đã được sử dụng ở nơi khác");
            }
        }
        if (store.getCreatedtime() == null) {
            store.setCreatedtime(new Date()); // Sử dụng Date để thiết lập thời gian tạo
        }
        if (store.getTaxcode() == null || store.getTaxcode().isEmpty()) {
            store.setTaxcode(null);
        }

        // Gán giá trị tên cửa hàng cho slug
        store.setSlug(slugText.generateUniqueSlug(store.getNamestore()));
        store.setBlock(false);
        store.setStatus("Không hiệu lực");

        // Tìm user
        User user = userRepository.findById(store.getUser().getId()).orElseThrow();
        if (user.getRolePermission().getId() == 6) {
            RolePermission newRolePermission = rolePermissionReponsitory.findById(5).orElseThrow();
            user.setRolePermission(newRolePermission);
        }

        userRepository.save(user); // Cập nhật lại role khi tạo store
        store.setUser(user);// Cập nhật lại user khi tạo store
        Store savedStore = storeRepository.save(store);
        return ResponseEntity.ok(savedStore);
    }

    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop')")
    @PutMapping("/store/{id}")
    public ResponseEntity<?> put(@PathVariable("id") Integer id, @RequestPart("store") String storeJson,
            @RequestPart(value = "imgbackgound", required = false) MultipartFile imgbackgound) {
        if (!storeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Chuyển đổi
        ObjectMapper objectMapper = new ObjectMapper();
        Store store;
        try {
            store = objectMapper.readValue(storeJson, Store.class);
            // Bắt lỗi
            ResponseEntity<String> validateRes = validate(store);
            if (validateRes != null) {
                return validateRes;
            }

            if (store.getTaxcode() == null || store.getTaxcode().isEmpty()) {
                store.setTaxcode(null);
            }

            Integer countTaxCode = storeRepository.checkDuplicate(store.getTaxcode(), store.getId());
            if (countTaxCode > 0 && !store.getTaxcode().isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Mã thuế đã được sử dụng");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Không thể chuyển đổi dữ liệu store: " + e.getMessage());
        }

        store.setId(id);
        Store existingStore = storeRepository.findById(store.getId()).orElseThrow();
        // Kiểm tra sự tồn tại của slug
        if (!store.getSlug().isEmpty() || store.getSlug() != null) {
            store.setSlug(slugText.generateUniqueSlug(store.getNamestore()));
        }

        // if (store.getCreatedtime() == null) {
        // store.setCreatedtime(LocalDateTime.now());
        // }

        String oldImageUrl = existingStore.getImgbackgound();
        // Giải mã URL trước
        String decodedUrl = java.net.URLDecoder.decode(oldImageUrl,
                java.nio.charset.StandardCharsets.UTF_8);

        // Loại bỏ phần https://firebasestorage.googleapis.com/v0/b/ và lấy phần sau o/
        String filePath = decodedUrl.split("o/")[1]; // Tách phần sau "o/"

        // Loại bỏ phần ?alt=media
        int queryIndex = filePath.indexOf("?"); // Tìm vị trí của dấu hỏi "?"
        if (queryIndex != -1) {
            filePath = filePath.substring(0, queryIndex); // Cắt bỏ phần sau dấu hỏi
        }

        if (imgbackgound != null && !imgbackgound.isEmpty()) {
            try {
                // Lưu hình ảnh mới lên Firebase và lấy URL
                String newAvatarUrl = firebaseStorageService.uploadToFirebase(imgbackgound,
                        "stores");

                // Xóa ảnh cũ trên Firebase nếu có
                if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                    try {
                        firebaseStorageService.deleteFileFromFirebase(filePath);
                    } catch (Exception e) {
                        System.err.println("Không thể xóa ảnh cũ trên Firebase: " + e.getMessage());
                    }
                }

                // Cập nhật avatar mới
                store.setImgbackgound(newAvatarUrl);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Không thể lưu hình ảnh: " + e.getMessage());
            }
        } else {
            store.setImgbackgound(existingStore.getImgbackgound());

        }

        try {
            Store storeUpdate = storeRepository.save(store);
            return ResponseEntity.ok(storeUpdate);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Không thể cập nhật cửa hàng: " + e.getMessage());
        }
    }

    // Delete
    @PreAuthorize("hasAnyAuthority('Admin_All_Function', 'Admin_Manage_Support')")
    @DeleteMapping("/store/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        // TODO: process PUT request
        if (!storeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        storeRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Danh mục trong cửa hàng
    @GetMapping("/CateProductInStore/{id}")
    public ResponseEntity<List<ProductCategory>> getCateProById(@PathVariable("id") Integer idStore) {
        List<ProductCategory> productCategories = categoryRepository.cateProductInStore(idStore);
        return ResponseEntity.ok(productCategories);
    }

    @GetMapping("/business/{taxcode}")
    public ResponseEntity<?> getBusinessInfo(@PathVariable String taxcode) {
        String url = "https://api.vietqr.io/v2/business/" + taxcode;
        RestTemplate restTemplate = new RestTemplate();
        try {
            String result = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Mã không hợp lệ");
        }
    }

    // put for admin
    @PreAuthorize("hasAnyAuthority('Admin_All_Function', 'Admin_Manage_Support')")
    @PutMapping("/store/ban/{id}")
    public ResponseEntity<?> putStoreForAdmin(@PathVariable("id") Integer id, @RequestBody Store storeRequest) {
        Store store = storeRepository.findById(id).orElse(null);
        if (store.getId() == null || store == null) {
            return ResponseEntity.notFound().build();
        }
        // cập nhật lại thông tin
        store.setBlock(storeRequest.getBlock());
        store.setStatus(storeRequest.getStatus());
        store.setStartday(storeRequest.getStartday());
        store.setEndday(storeRequest.getEndday());
        store.setReason(storeRequest.getReason());

        Store savedStore = storeRepository.save(store);
        return ResponseEntity.ok(savedStore);

    }

    // all sản phẩm bán chạy store (ProductList)
    @PreAuthorize("hasAnyAuthority('Admin_All_Function', 'Admin_Manage_Revenue')")
    @GetMapping("/product-sales")
    public List<Map<String, Object>> getProductSalesByStore() {
        // Gọi phương thức findProductSalesByStore từ StoreRepository
        return storeRepository.findProductSalesByStore();
    }

    // Doanh thu cửa all cửa hàng bên admin (ProductList)
    @PreAuthorize("hasAnyAuthority('Admin_All_Function', 'Admin_Manage_Revenue')")
    @GetMapping("/revenue/net-store-revenue")
    public List<Map<String, Object>> getNetRevenueByStore() {
        return storeRepository.findNetRevenueByStore();
    }

    // Doanh thu theo năm
    @PreAuthorize("hasAnyAuthority('Admin_All_Function', 'Admin_Manage_Revenue')")
    @GetMapping("/revenue-by-year")
    public ResponseEntity<List<Map<String, Object>>> getRevenueByYear() {
        List<Map<String, Object>> revenueData = storeRepository.findRevenueByYear();
        return ResponseEntity.ok(revenueData);

    }

    // Doanh thu theo tháng Dashboard
    @PreAuthorize("hasAnyAuthority('Admin_All_Function', 'Admin_Manage_Revenue')")
    @GetMapping("/revenue-by-month")
    public ResponseEntity<List<Map<String, Object>>> getVATByMonth(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        List<Map<String, Object>> vatData = storeRepository.findTotalVATByMonth(startDate, endDate);
        return ResponseEntity.ok(vatData);
    }

    // Doanh thu theo ngày
    @PreAuthorize("hasAnyAuthority('Admin_All_Function', 'Admin_Manage_Revenue')")
    @GetMapping("/revenue-by-day")
    public ResponseEntity<List<Map<String, Object>>> getRevenueByDay(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<Map<String, Object>> revenueData = storeRepository.findRevenueByDay(startDate, endDate);
        return ResponseEntity.ok(revenueData);
    }

    // Tổng cửa hàng được tạo (card số lượng cửa hàng) Dashboard
    @PreAuthorize("hasAnyAuthority('Admin_All_Function', 'Admin_Manage_Revenue')")
    @GetMapping("/total-stores-count")
    public ResponseEntity<Map<String, Long>> getTotalStoresCount() {
        long totalStoresCount = storeRepository.countTotalStores();
        Map<String, Long> response = new HashMap<>();
        response.put("totalStoresCount", totalStoresCount);
        return ResponseEntity.ok(response);
    }

    // Số lượng cửa hàng theo tháng
    @PreAuthorize("hasAnyAuthority('Admin_All_Function', 'Admin_Manage_Revenue')")
    @GetMapping("/stores-by-month")
    public ResponseEntity<List<Map<String, Object>>> getCountStoresByMonth() {
        List<Map<String, Object>> storeCountData = storeRepository.countStoresByMonth();
        return ResponseEntity.ok(storeCountData);
    }

    // Số lượng cửa hàng theo ngày
    @PreAuthorize("hasAnyAuthority('Admin_All_Function', 'Admin_Manage_Revenue')")
    @GetMapping("/stores-by-day")
    public ResponseEntity<List<Map<String, Object>>> getCountStoresByDay() {
        List<Map<String, Object>> storeCountData = storeRepository.countStoresByDay();
        return ResponseEntity.ok(storeCountData);
    }

    // Bắt lỗi
    public ResponseEntity<String> validate(Store store) {
        // Biểu thức chính quy email
        String patternEmail = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
        // Biểu thức chính quy số điện thoại
        String patternPhone = "0[0-9]{9}";
        // Biểu thức chính quy căn cước
        String patternCccd = "^[0-9]{9,12}$";

        // Tên cửa hàng
        if (store.getNamestore().isEmpty()) {
            return ResponseEntity.badRequest().body("Không được bỏ trống tên cửa hàng");
        } else if (store.getNamestore().length() < 10) {
            return ResponseEntity.badRequest().body("Tên không hợp lệ");
        }

        // Địa chỉ
        if (store.getAddress().isEmpty()) {
            return ResponseEntity.badRequest().body("Không được bỏ trống địa chỉ");
        }
        // email
        if (store.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("Không được bỏ trống email");
        } else if (!store.getEmail().matches(patternEmail)) {
            return ResponseEntity.badRequest().body("Email không hợp lệ");
        }

        // Số điện thoại
        if (store.getPhone().isEmpty()) {
            return ResponseEntity.badRequest().body("Không được trống số điện thoại");
        } else if (!store.getPhone().matches(patternPhone)) {
            return ResponseEntity.badRequest().body("Số điện thoại không hợp lệ");
        }

        // Căn cước công dân
        if (store.getCccdnumber().isEmpty()) {
            return ResponseEntity.badRequest().body("Không được bỏ trống số căn cước công dân");
        } else if (!store.getCccdnumber().matches(patternCccd)) {
            return ResponseEntity.badRequest().body("Số căn cước không hợp lệ");
        }

        // Mã sô thuế
        if (store.getTaxcode() == null) {
            return null;
        } else if (!String.valueOf(store.getTaxcode()).matches("^\\d{10}$|^\\d{13}$")
                && !store.getTaxcode().isEmpty()) {
            return ResponseEntity.badRequest().body("Mã số thuế không hợp lệ");
        }

        return null;
    }

}
