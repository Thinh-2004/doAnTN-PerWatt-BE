package com.duantn.be_project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Repository.ProductDetailRepository;
import com.duantn.be_project.Repository.ProductRepository;
import com.duantn.be_project.Repository.VoucherAdminDetailRepository;
import com.duantn.be_project.Repository.VouchersAdminRepository;
import com.duantn.be_project.model.Product;
import com.duantn.be_project.model.ProductDetail;
import com.duantn.be_project.model.VoucherAdmin;
import com.duantn.be_project.model.VoucherAdminDetail;

import jakarta.transaction.Transactional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@CrossOrigin("*")
public class VoucherAdminDetailController {
    @Autowired
    VoucherAdminDetailRepository voucherAdminDetailRepository;
    @Autowired
    VouchersAdminRepository vouchersAdminRepository;
    @Autowired
    ProductRepository productRepository;

    // GetVoucherDetailAdminByIdStore
    // @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop')")
    // @GetMapping("voucherAdminDetails/list/{idStore}")
    // public ResponseEntity<List<VoucherAdminDetail>> getMethodName(@PathVariable Integer idStore) {
    //     List<VoucherAdminDetail> voucherAdminDetails = voucherAdminDetailRepository.findAllByidStore(idStore);
    //     if (voucherAdminDetails == null) {
    //         return ResponseEntity.notFound().build();
    //     }

    //     return ResponseEntity.ok(voucherAdminDetails);
    // }

    // Widget save
    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop')")
    @PostMapping("/api/voucherAdminDetails/create")
public ResponseEntity<?> createVoucherAdminDetails(@RequestBody List<VoucherAdminDetail> voucherAdminDetails) {
    if (voucherAdminDetails == null || voucherAdminDetails.isEmpty()) {
        return ResponseEntity.badRequest().body("Danh sách VoucherAdminDetail trống!");
    }

    // Lấy VoucherAdmin từ ID được gửi từ client thông qua mỗi đối tượng VoucherAdminDetail
    int idVoucherAdmin = voucherAdminDetails.get(0).getVoucherAdmin().getId(); // Giả sử idVoucherAdmin được lấy từ đối tượng đầu tiên

    // Tìm VoucherAdmin từ cơ sở dữ liệu
VoucherAdmin voucherAdmin = vouchersAdminRepository.findById(idVoucherAdmin)
            .orElseThrow(() -> new RuntimeException("VoucherAdmin not found with ID: " + idVoucherAdmin));

    // Lưu từng chi tiết voucher vào cơ sở dữ liệu
    for (VoucherAdminDetail voucherAdminDetail : voucherAdminDetails) {
        // Kiểm tra sản phẩm chi tiết có tồn tại không
        Product product = productRepository.findById(voucherAdminDetail.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + voucherAdminDetail.getProduct().getId()));

        // Thiết lập lại các thông tin cần thiết cho VoucherAdminDetail
        voucherAdminDetail.setVoucherAdmin(voucherAdmin); // Thiết lập VoucherAdmin từ cơ sở dữ liệu
        voucherAdminDetail.setProduct(product); // Thiết lập sản phẩm từ cơ sở dữ liệu

        // Lưu VoucherAdminDetail vào cơ sở dữ liệu
        voucherAdminDetailRepository.save(voucherAdminDetail);
    }

    return ResponseEntity.status(HttpStatus.CREATED).body("Lưu danh sách thành công!");
}

    // Cập nhật giá giảm cho sản phẩm theo ProductDetail ID
    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop')")
    @PutMapping("/voucherAdminDetails/updateDiscountByProduct/{idProduct}")
    @Transactional
    public ResponseEntity<?> updateDiscountByProduct(
            @PathVariable Integer idProduct,
            @RequestParam Float newDiscountPrice) {
        try {
            // Tìm VoucherAdminDetail theo idProduct
            List<VoucherAdminDetail> details = voucherAdminDetailRepository.findByIdProduct(idProduct);
    
            if (details.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy VoucherAdminDetail cho sản phẩm này");
            }
    
            // Cập nhật giá giảm cho từng chi tiết và lưu lại
            details.forEach(detail -> detail.setDiscountprice(newDiscountPrice));
    
            // Sử dụng saveAll để lưu tất cả chi tiết cùng một lúc
            voucherAdminDetailRepository.saveAll(details);
    
            return ResponseEntity.ok("Cập nhật giá giảm thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi cập nhật giá giảm: " + e.getMessage());
        }
    }
    
    

    // // Xóa voucherAdminDetails theo ProductDetail ID
    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop')")
    @DeleteMapping("/voucherAdminDetails/deleteByProduct/{idProduct}")
@Transactional
public ResponseEntity<?> deleteVoucherAdminDetailsByProduct(@PathVariable Integer idProduct) {
    try {
        voucherAdminDetailRepository.deleteByProduct(idProduct);
        return ResponseEntity.ok("Deleted successfully");
    } catch (Exception e) {
return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting voucher details");
    }
}

    

}