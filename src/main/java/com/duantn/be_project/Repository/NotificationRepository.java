package com.duantn.be_project.Repository;

import com.duantn.be_project.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Order, Integer> {

    @Query("SELECT DISTINCT o.user.id FROM Order o")
    List<Integer> findAllUserIds();

    @Query(value = "SELECT * FROM Orders WHERE StoreId = 2 AND orderstatus like N'Đang chờ duyệt'" , nativeQuery = true)
    List<Order> findAllStoreId(Integer storeId);

}
