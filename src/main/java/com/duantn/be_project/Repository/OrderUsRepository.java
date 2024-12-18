package com.duantn.be_project.Repository;

import com.duantn.be_project.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface OrderUsRepository extends JpaRepository<Order, Integer> {
        @Query(value = "SELECT o.orderStatus AS orderStatus, COUNT(*) AS count " +
                        "FROM Orders o " +
                        "WHERE o.storeId = :storeId " +
                        "AND (" +
                        "   (o.orderStatus = 'Hoàn thành' AND (:endDate IS NOT NULL AND o.receivedate BETWEEN :startDate AND :endDate)) OR "
                        +
                        "   (o.orderStatus != 'Hoàn thành' AND o.paymentDate >= :startDate)" +
                        ") " +
                        "GROUP BY o.orderStatus", nativeQuery = true)
        List<Map<String, Object>> countOrdersByStatusForStore(
                        @Param("storeId") Integer storeId,
                        @Param("startDate") Date startDate,
                        @Param("endDate") Date endDate);
}