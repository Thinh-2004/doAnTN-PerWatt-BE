package com.duantn.be_project.model.Request_Response;

import java.util.List;

import com.duantn.be_project.model.Product;
import com.duantn.be_project.model.ProductDetail;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ProductDTO {
    Product product;
    List<ProductDetail> productDetails;
    Integer countOrderSuccess;
}
