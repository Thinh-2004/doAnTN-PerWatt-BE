package com.duantn.be_project.model.Request;

import java.util.List;

import com.duantn.be_project.model.Order;
import com.duantn.be_project.model.OrderDetail;

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