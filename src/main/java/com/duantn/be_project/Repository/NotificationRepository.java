package com.duantn.be_project.Repository;

import com.duantn.be_project.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Order, Integer> {

    // Truy vấn buyer
    @Query("SELECT DISTINCT o.user.id FROM Order o")
    List<Integer> findAllUserIds();

    @Query(value = "SELECT * FROM Orders WHERE UserId = ?1 AND orderstatus IN (N'Đang vận chuyển', N'Hủy')", nativeQuery = true)
    List<Order> findAllReadyToShipOrCanceledOrders(Integer userId);

    // Truy vấn seller
    @Query(value = "SELECT * FROM Orders WHERE StoreId = 2 AND orderstatus like N'Đang chờ duyệt'", nativeQuery = true)
    List<Order> findAllStoreId(Integer storeId);

    @Query(value = "SELECT * FROM Orders WHERE StoreId = :storeId AND orderstatus = N'Hoàn thành'", nativeQuery = true)
    List<Order> findDeliveredOrdersByStoreId(Integer storeId);

    // New query for canceled orders
    @Query(value = "SELECT * FROM Orders WHERE StoreId = :storeId AND orderstatus = N'Hủy'", nativeQuery = true)
    List<Order> findCanceledOrdersByStoreId(Integer storeId);

}
