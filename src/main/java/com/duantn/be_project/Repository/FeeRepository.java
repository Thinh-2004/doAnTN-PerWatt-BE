package com.duantn.be_project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.duantn.be_project.model.Fee;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Integer> {
    @Query("SELECT SUM(CASE " +
           "WHEN YEAR(o.paymentdate) > :currentYear THEN od.price * 0.9 * 1.1 " +
           "ELSE od.price * 0.9 " +
           "END) AS totalRevenue " +
           "FROM Fee f " +
           "JOIN f.orders o " +
           "JOIN o.orderdetails od " +
           "WHERE f.id = :feeId")
    Float calculateTotalRevenueAfterTax(@Param("feeId") Integer feeId, @Param("currentYear") Integer currentYear);
}
