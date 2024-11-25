package com.duantn.be_project.controller;

import java.util.List;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Repository.ProductDetailRepository;
import com.duantn.be_project.Repository.VoucherAdminDetailRepository;
import com.duantn.be_project.Repository.VouchersAdminRepository;
import com.duantn.be_project.model.ProductDetail;
import com.duantn.be_project.model.VoucherAdmin;
import com.duantn.be_project.model.VoucherAdminDetail;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@CrossOrigin("*")
public class VoucherAdminDetailController {
    @Autowired
    VouchersAdminRepository vouchersAdminRepository;
    @Autowired
    ProductDetailRepository productDetailRepository;
    @Autowired
    VoucherAdminDetailRepository voucherAdminDetailRepository;

    // Widget save
    @PreAuthorize("hasAnyAuthority('Seller', 'Admin')")
    @PostMapping("/api/voucherAdminDetails/create")
    public ResponseEntity<?> createVoucherAdminDetails(@RequestBody List<VoucherAdminDetail> voucherAdminDetails) {
        if (voucherAdminDetails == null || voucherAdminDetails.isEmpty()) {
            return ResponseEntity.badRequest().body("Danh sách VoucherAdminDetail trống!");
        }

        // Lấy VoucherAdmin từ ID được gửi từ client
        int idVoucherAdmin = voucherAdminDetails.get(0).getVoucherAdmin().getId(); 

        // Tìm VoucherAdmin từ cơ sở dữ liệu
        VoucherAdmin voucherAdmin = vouchersAdminRepository.findById(idVoucherAdmin)
                .orElseThrow(() -> new RuntimeException("VoucherAdmin not found with ID: " + idVoucherAdmin));

        // Lưu từng chi tiết voucher vào cơ sở dữ liệu
        for (VoucherAdminDetail voucherAdminDetail : voucherAdminDetails) {
            // Kiểm tra sản phẩm chi tiết có tồn tại không
            ProductDetail productDetail = productDetailRepository
                    .findById(voucherAdminDetail.getProductDetail().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "ProductDetail not found with ID: " + voucherAdminDetail.getProductDetail().getId()));

            voucherAdminDetail.setProductDetail(productDetail);
            voucherAdminDetail.setVoucherAdmin(voucherAdmin); // Thiết lập VoucherAdmin

            voucherAdminDetailRepository.save(voucherAdminDetail);
        }

        return ResponseEntity.status(HttpStatus.SC_CREATED).body("Lưu danh sách VoucherAdminDetail thành công!");
    }
}
