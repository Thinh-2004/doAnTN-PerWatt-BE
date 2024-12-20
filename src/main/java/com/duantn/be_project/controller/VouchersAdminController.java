package com.duantn.be_project.controller;

import com.duantn.be_project.model.VoucherAdmin;
import com.duantn.be_project.model.VoucherAdminCategory;
import com.duantn.be_project.model.VoucherAdminDetail;
import com.duantn.be_project.model.OrderDetail;
import com.duantn.be_project.model.Product;
import com.duantn.be_project.model.ProductCategory;
import com.duantn.be_project.model.ProductDetail;
import com.duantn.be_project.Repository.VouchersAdminRepository;
import com.duantn.be_project.Repository.CategoryRepository;
import com.duantn.be_project.Repository.OrderDetailRepository;
import com.duantn.be_project.Repository.ProductDetailRepository;
import com.duantn.be_project.Repository.VoucherAdminCategoryRepository;
import com.duantn.be_project.Repository.VoucherAdminDetailRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class VouchersAdminController {

    @Autowired
    private VouchersAdminRepository vouchersAdminRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private VoucherAdminDetailRepository voucherAdminDetailRepository;

    @Autowired
    private VoucherAdminCategoryRepository voucherAdminCategoryRepository;

    // Lấy tất cả voucher
    @PreAuthorize("hasAnyAuthority('Admin_Manage_Promotion','Admin_All_Function','Seller_Manage_Shop')")
    @GetMapping("/vouchersAdmin")
    public ResponseEntity<List<VoucherAdmin>> getAllVouchers() {
        List<VoucherAdmin> vouchers = vouchersAdminRepository.findAll();
        // if (vouchers.isEmpty()) {
        // return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        // }
        return ResponseEntity.ok(vouchers);
    }

    @GetMapping("/vouchersAdminInPgaeHome")
    public ResponseEntity<List<VoucherAdmin>> getAllVouchersInPageHome() {
        List<VoucherAdmin> vouchers = vouchersAdminRepository.findAll();
        // if (vouchers.isEmpty()) {
        // return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        // }
        return ResponseEntity.ok(vouchers);
    }

    // Thêm mới voucher
    @PreAuthorize("hasAnyAuthority('Admin_Manage_Promotion','Admin_All_Function')")
    @PostMapping("/vouchersAdmin/create")
    public ResponseEntity<VoucherAdmin> createVoucher(@RequestBody VoucherAdmin voucher) {
        if (voucher.getVoucherAdminDetails() != null) {
            for (VoucherAdminDetail detail : voucher.getVoucherAdminDetails()) {
                detail.setVoucherAdmin(voucher);
            }
        } else {
            voucher.setVoucherAdminDetails(new ArrayList<>());
        }

        try {
            VoucherAdmin newVoucher = vouchersAdminRepository.save(voucher);
            System.out.println("Voucher saved successfully: " + newVoucher);
            return new ResponseEntity<>(newVoucher, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Error saving voucher: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Lấy voucher theo idVoucherAdmin
    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop')")
    @GetMapping("/vouchersAdmin/{id}")
    public ResponseEntity<VoucherAdmin> getVoucherById(@PathVariable Integer id) {
        return vouchersAdminRepository.findById(id)
                .map(voucher -> ResponseEntity.ok(voucher))
                .orElseGet(() -> {
                    System.out.println("Voucher với ID " + id + " không tồn tại.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                });
    }

    // Lấy danh sách danh mục khuyến mãi
    @PreAuthorize("hasAnyAuthority('Admin_Manage_Promotion','Admin_All_Function')")
    @GetMapping("/voucher-categories")
    public ResponseEntity<List<VoucherAdminCategory>> getVoucherCategories() {
        List<VoucherAdminCategory> categories = voucherAdminCategoryRepository.findAll();
        if (categories.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(categories);
    }

    // Cập nhật trạng thái của VoucherAdmin
    @PreAuthorize("hasAnyAuthority('Admin_Manage_Promotion','Admin_All_Function')")
    @PutMapping("/vouchersAdmin/{id}/status")
    public ResponseEntity<?> updateVoucherStatus(@PathVariable int id, @RequestBody Map<String, String> statusUpdate) {
        String newStatus = statusUpdate.get("status");
        return vouchersAdminRepository.findById(id).map(voucher -> {
            try {
                voucher.setStatus(newStatus);
                vouchersAdminRepository.save(voucher);
                System.out.println("Cập nhật trạng thái voucher với ID " + id + " thành " + newStatus);
                return ResponseEntity.ok("Cập nhật trạng thái thành công.");
            } catch (Exception e) {
                System.err.println("Lỗi khi cập nhật trạng thái voucher: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Có lỗi xảy ra khi cập nhật trạng thái.");
            }
        }).orElseGet(() -> {
            System.out.println("Voucher với ID " + id + " không tồn tại.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Voucher không tồn tại.");
        });
    }

    // // Cập nhật voucher
    @PreAuthorize("hasAnyAuthority('Admin_Manage_Promotion','Admin_All_Function')")
    @PutMapping("/vouchersAdmin/{id}")
    public ResponseEntity<VoucherAdmin> updateVoucher(@PathVariable int id, @RequestBody VoucherAdmin voucher) {
        return vouchersAdminRepository.findById(id)
                .map(existingVoucher -> {
                    existingVoucher.setVouchername(voucher.getVouchername());
                    existingVoucher.setStartday(voucher.getStartday());
                    existingVoucher.setEndday(voucher.getEndday());
                    existingVoucher.setVoucherAdminCategory(voucher.getVoucherAdminCategory());

                    if (voucher.getVoucherAdminDetails() != null) {
                        existingVoucher.setVoucherAdminDetails(voucher.getVoucherAdminDetails());
                    }

                    vouchersAdminRepository.save(existingVoucher);
                    return ResponseEntity.ok(existingVoucher);
                })
                .orElseGet(() -> {
                    System.out.println("Voucher với ID " + id + " không tồn tại.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                });
    }

    // Xóa voucher theo ID
    @PreAuthorize("hasAnyAuthority('Admin_Manage_Promotion','Admin_All_Function')")
    @DeleteMapping("/vouchersAdmin/{id}")
    @Transactional
    public ResponseEntity<?> deleteVoucherById(@PathVariable int id) {
        if (!vouchersAdminRepository.existsById(id)) {
            System.out.println("Voucher với ID " + id + " không tồn tại.");
            return ResponseEntity.notFound().build();
        }

        try {
            System.out.println("Bắt đầu xóa chi tiết liên quan đến voucher...");
            voucherAdminDetailRepository.deleteByVoucherAdminId(id);
            System.out.println("Chi tiết liên quan đã được xóa thành công.");

            System.out.println("Bắt đầu xóa voucher...");
            vouchersAdminRepository.deleteById(id);
            System.out.println("Voucher với ID " + id + " đã được xóa thành công.");

            return ResponseEntity.ok("Voucher đã được xóa thành công.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi trong quá trình xóa voucher với ID " + id + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi xóa voucher.");
        }
    }

    // // Lấy sản phẩm theo ID
    // @PreAuthorize("hasAnyAuthority('Admin')")
    // @GetMapping("/products")
    // public ResponseEntity<List<ProductDetail>> getProductsByIds(@RequestParam
    // List<Integer> ids) {
    // List<ProductDetail> products = productDetailRepository.findAllById(ids);
    // if (products.isEmpty()) {
    // return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    // }
    // return ResponseEntity.ok(products);
    // }

    // // Lấy chi tiết voucher theo voucherId
    // @PreAuthorize("hasAnyAuthority('Admin')")
    // @GetMapping("/voucherAdminDetails/{voucherAdminId}")
    // public ResponseEntity<List<VoucherAdminDetail>> getVoucherAdminDetails(
    // @PathVariable("voucherAdminId") Integer voucherAdminId) {
    // List<VoucherAdminDetail> details =
    // voucherAdminDetailRepository.findByVoucherAdminId(voucherAdminId);
    // if (details.isEmpty()) {
    // return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    // }
    // return ResponseEntity.ok(details);
    // }

    // // Cập nhật trạng thái voucher mỗi ngày lúc 00:00 @Scheduled(cron = "0 0 0 *
    // *
    // // ?")
    @Scheduled(cron = "0 0/2 * * * ?")
    public void updateVoucherStatuses() {
        List<VoucherAdmin> vouchers = vouchersAdminRepository.findAll();
        Date currentDate = new Date();

        for (VoucherAdmin voucher : vouchers) {
            String newStatus = "không hoạt động"; // Mặc định là không hoạt động

            Date startDate = voucher.getStartday();
            Date endDate = voucher.getEndday();

            // Nếu currentDate nhỏ hơn startDate, voucher sẽ "đang hoạt động"
            if (currentDate.before(startDate)) {
                newStatus = "đang hoạt động";
            }
            // Nếu currentDate lớn hơn hoặc bằng startDate và nhỏ hơn hoặc bằng endDate,
            // voucher sẽ "đang hoạt động"
            else if (!currentDate.before(startDate) && !currentDate.after(endDate)) {
                newStatus = "đang hoạt động";
            }
            // Nếu currentDate lớn hơn endDate, voucher sẽ "không hoạt động"
            else if (currentDate.after(endDate)) {
                newStatus = "không hoạt động";
            }

            // Nếu trạng thái thay đổi, lưu lại voucher
            if (!newStatus.equals(voucher.getStatus())) {
                voucher.setStatus(newStatus);
                vouchersAdminRepository.save(voucher);
            }
        }
    }

    ///
    
    @GetMapping("/products")
    public List<VoucherAdminDetail> getAllRegisteredProducts() {
        // Trả về tất cả sản phẩm đã đăng ký
        return voucherAdminDetailRepository.findAll();
    }

    @GetMapping("/product/quantity/{idproduct}")
    public Integer getQuantityByProductId(@PathVariable("idproduct") Integer productId) {
        // Lấy danh sách chi tiết đơn hàng dựa trên productId
        List<OrderDetail> orderDetails = orderDetailRepository.findByProductDetailProductId(productId);

        // Tính tổng quantity từ các OrderDetail
        return orderDetails.stream()
                .mapToInt(OrderDetail::getQuantity)
                .sum(); // Trả về tổng số lượng
    }

    @PreAuthorize("hasAnyAuthority('Admin_Manage_Promotion','Admin_All_Function','Seller_Manage_Shop')")
    @GetMapping("/flash-sales")
    public ResponseEntity<List<Map<String, Object>>> getAllVoucherAdminDetailsWithProduct() {
        try {
            // Lấy tất cả VoucherAdminDetails từ cơ sở dữ liệu
            List<VoucherAdminDetail> voucherAdminDetails = voucherAdminDetailRepository.findAll();

            if (voucherAdminDetails.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            List<Map<String, Object>> result = new ArrayList<>();

            // Duyệt qua các VoucherAdminDetail và thêm thông tin vào response
            for (VoucherAdminDetail voucherAdminDetail : voucherAdminDetails) {
                Map<String, Object> response = new HashMap<>();
                response.put("voucherAdminDetail", voucherAdminDetail);

                // Thêm thông tin sản phẩm từ VoucherAdminDetail (giả sử bạn có thông tin sản
                // phẩm trong VoucherAdminDetail)
                if (voucherAdminDetail.getProduct() != null) {
                    response.put("product", voucherAdminDetail.getProduct()); // Trả về thông tin sản phẩm từ
                                                                              // VoucherAdminDetail
                } else {
                    response.put("product", null);
                }

                result.add(response);
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}