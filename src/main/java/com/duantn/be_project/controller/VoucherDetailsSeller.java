package com.duantn.be_project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Repository.VoucherDetailsSellerRepository;
import com.duantn.be_project.Repository.VoucherSellerRepository;
import com.duantn.be_project.model.Voucher;
import com.duantn.be_project.model.VoucherDetail;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@CrossOrigin("*")
public class VoucherDetailsSeller {
    @Autowired
    VoucherDetailsSellerRepository voucherDetailsSellerRepository;
    @Autowired
    VoucherSellerRepository voucherSellerRepository;

    @PreAuthorize("hasAnyAuthority('Seller', 'Buyer')") // Chỉ vai trò là seller mới được gọi
    @GetMapping("/findVoucherByIdUser/{id}")
    public ResponseEntity<?> getMethodName(@PathVariable("id") Integer id) {
        List<VoucherDetail> voucherDetails = voucherDetailsSellerRepository.findAllByIdAUser(id);
        return ResponseEntity.ok(voucherDetails);
    }

    @PreAuthorize("hasAnyAuthority('Seller', 'Buyer')")
    @PostMapping("addVoucherDetails")
    public ResponseEntity<?> postVoucherDeltails(@RequestBody VoucherDetail voucherDetailRequest) {
        List<Voucher> vouchers = voucherSellerRepository
                .findByVoucherName(voucherDetailRequest.getVoucher().getVouchername());
        for (Voucher voucher : vouchers) {
            VoucherDetail voucherDetail = new VoucherDetail();
            voucherDetail.setUser(voucherDetailRequest.getUser());
            voucherDetail.setVoucher(voucher);

            // Lưu voucherDetail
            voucherDetailsSellerRepository.save(voucherDetail);
        }
        return ResponseEntity.ok("Lưu thành công");
    }

}
