package com.duantn.be_project.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.duantn.be_project.model.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    @Query("select od from OrderDetail od where od.order.id = ?1")
    List<OrderDetail> findAllOrderDetailByIdOrder(Integer idOrder);

    List<OrderDetail> findByProductDetailProductId(Integer productId);
}