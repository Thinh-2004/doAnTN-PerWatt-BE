package com.duantn.be_project.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class OrderRequest {
    private Order order;
    private List<OrderDetail> orderDetails;

}