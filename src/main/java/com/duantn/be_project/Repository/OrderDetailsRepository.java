package com.duantn.be_project.Repository;

import com.duantn.be_project.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetail, Integer> {

    @Query(value = "SELECT " +
                   "s.namestore AS StoreName, " +
                   "YEAR(o.paymentdate) AS Year, " +
                   "COUNT(od.id) AS TotalOrderDetails " +
                   "FROM Stores s " +
                   "JOIN Products p ON s.id = p.storeId " +
                   "JOIN OrderDetails od ON p.id = od.productid " +
                   "JOIN Orders o ON od.orderid = o.id " +
                   "GROUP BY s.namestore, YEAR(o.paymentdate) " +
                   "ORDER BY s.namestore, Year", 
           nativeQuery = true)
    List<Map<String, Object>> countOrderDetailsByStoreAndYear();
}
