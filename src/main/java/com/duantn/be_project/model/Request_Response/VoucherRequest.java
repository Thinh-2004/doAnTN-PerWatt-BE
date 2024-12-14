package com.duantn.be_project.model.Request_Response;

import java.util.List;

import com.duantn.be_project.model.Product;

import com.duantn.be_project.model.Voucher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VoucherRequest {
    Voucher voucher;
    List<Product> products;
}
