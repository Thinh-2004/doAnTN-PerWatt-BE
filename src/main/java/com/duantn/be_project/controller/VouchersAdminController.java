package com.duantn.be_project.controller;

import com.duantn.be_project.model.VoucherAdmin;
import com.duantn.be_project.Repository.VouchersAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vouchers")  // Đường dẫn API
public class VouchersAdminController {

    @Autowired
    private VouchersAdminRepository vouchersAdminRepository;  // Tiêm repository

    // Phương thức tạo voucher
    @PostMapping("/create")
    public ResponseEntity<VoucherAdmin> createVoucher(@RequestBody VoucherAdmin voucher) {
        // Lưu voucher vào cơ sở dữ liệu
        VoucherAdmin newVoucher = vouchersAdminRepository.save(voucher);
        return new ResponseEntity<>(newVoucher, HttpStatus.CREATED);  // Trả về phản hồi với mã trạng thái 201
    }
}
