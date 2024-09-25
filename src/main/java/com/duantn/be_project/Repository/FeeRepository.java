package com.duantn.be_project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.duantn.be_project.model.Fee;
import java.util.List;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Integer> {

    @Query("SELECT s.namestore, f.taxmoney " +
           "FROM Store s " +
           "JOIN Order o ON s.id = o.shippinginfor.id " +
           "JOIN Fee f ON o.fee.id = f.id " +
           "GROUP BY s.namestore, f.taxmoney")
    List<Object[]> findStoreAndFeeDetails();
}