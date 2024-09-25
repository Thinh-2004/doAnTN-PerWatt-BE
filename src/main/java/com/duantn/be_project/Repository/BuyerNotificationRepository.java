package com.duantn.be_project.Repository;

import com.duantn.be_project.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuyerNotificationRepository extends JpaRepository<Order, Integer> {

    // Lấy tất cả đơn hàng theo storeId có trạng thái 'Chờ giao hàng'
    @Query(value = "SELECT * FROM Orders WHERE StoreId = ?1 AND orderstatus = N'Chờ giao hàng'", nativeQuery = true)
    List<Order> findAllReadyToShipOrders(Integer userId);

    // Lấy tất cả đơn hàng theo storeId có trạng thái 'Đã hủy'
    @Query(value = "SELECT * FROM Orders WHERE StoreId = ?1 AND orderstatus = N'Hủy'", nativeQuery = true)
    List<Order> findAllCanceledOrders(Integer userId);

}
