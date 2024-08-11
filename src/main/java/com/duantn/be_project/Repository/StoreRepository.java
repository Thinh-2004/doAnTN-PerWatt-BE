package com.duantn.be_project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.duantn.be_project.model.Store;

import java.util.List;
import java.util.Map;

public interface StoreRepository extends JpaRepository<Store, Integer> {

    @Query(value = "SELECT COUNT(*) FROM Stores", nativeQuery = true)
    long countTotalStores(); // Trả về kiểu long
    @Query("select s from Store s where s.user.id = ?1 ")
    public Store findStoreByIdUser(Integer idUser);
    boolean existsByNamestoreIgnoreCase(String namestore); 

    @Query(value = "SELECT s.namestore AS StoreName, SUM(od.quantity * od.price) * 0.9 AS TotalRevenue " +
                   "FROM Stores s " +
                   "JOIN Products p ON s.id = p.storeId " +
                   "JOIN OrderDetails od ON p.id = od.productId " +
                   "GROUP BY s.namestore", nativeQuery = true)
    List<Object[]> findTotalRevenueByStore();

    @Query(value = "SELECT YEAR(createdTime) AS Year, COUNT(*) AS TotalStores " +
                   "FROM Stores " +
                   "GROUP BY YEAR(createdTime) " +
                   "ORDER BY YEAR(createdTime)", nativeQuery = true)
    List<Map<String, Object>> countStoresByYear();

}
