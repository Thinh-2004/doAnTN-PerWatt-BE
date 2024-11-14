package com.duantn.be_project.model.Request_Response;

import java.util.List;

import com.duantn.be_project.model.ProductDetail;
import com.duantn.be_project.model.Voucher;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoucherRequest {
    Voucher voucher;
    List<ProductDetail> productDetails;
}
