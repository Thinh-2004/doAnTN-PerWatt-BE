package com.duantn.be_project.Repository;

import com.duantn.be_project.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OrderUsRepository extends JpaRepository<Order, Integer> {

    @Query(value = "SELECT s.nameStore, COUNT(*) AS TotalOrders " +
                   "FROM Orders o " +
                   "INNER JOIN Stores s ON o.StoreId = s.id " +
                   "WHERE o.orderStatus = 'Processing' " +
                   "GROUP BY s.nameStore", 
           nativeQuery = true)
    List<Map<String, Object>> countOrdersByStoreAndStatusProcessing();

    @Query(value = "SELECT s.nameStore, COUNT(*) AS TotalOrders " +
                   "FROM Orders o " +
                   "INNER JOIN Stores s ON o.StoreId = s.id " +
                   "WHERE o.orderStatus = 'Shipped' " +
                   "GROUP BY s.nameStore", 
           nativeQuery = true)
    List<Map<String, Object>> countOrdersByStoreAndStatusShipped();

    @Query(value = "SELECT s.nameStore, COUNT(*) AS TotalOrders " +
                   "FROM Orders o " +
                   "INNER JOIN Stores s ON o.StoreId = s.id " +
                   "WHERE o.orderStatus = 'Delivered' " +
                   "GROUP BY s.nameStore", 
           nativeQuery = true)
    List<Map<String, Object>> countOrdersByStoreAndStatusDelivered();

    @Query(value = "SELECT s.nameStore, COUNT(*) AS TotalOrders " +
                   "FROM Orders o " +
                   "INNER JOIN Stores s ON o.StoreId = s.id " +
                   "WHERE o.orderStatus = 'Cancelled' " +
                   "GROUP BY s.nameStore", 
           nativeQuery = true)
    List<Map<String, Object>> countOrdersByStoreAndStatusCancelled();

    @Query(value = "SELECT s.nameStore, COUNT(*) AS TotalOrders " +
                   "FROM Orders o " +
                   "INNER JOIN Stores s ON o.StoreId = s.id " +
                   "WHERE o.orderStatus = 'Returned' " +
                   "GROUP BY s.nameStore", 
           nativeQuery = true)
    List<Map<String, Object>> countOrdersByStoreAndStatusReturned();
}
