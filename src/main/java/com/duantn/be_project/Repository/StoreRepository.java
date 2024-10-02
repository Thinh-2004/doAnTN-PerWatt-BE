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

        // Kiểm tra taxcode khi create
        @Query("select count(s)  from Store s where s.taxcode = ?1")
        Integer checkDuplicate(String taxcode);

        // Kiểm tra taxcode khi update store
        @Query("select count(s)  from Store s where s.taxcode = ?1 and s.id != ?2")
        Integer checkDuplicate(String taxcode, Integer id);

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

     //Đếm sô lượng cửa hàng được tạo theo tháng
     @Query(value = "SELECT YEAR(createdTime) AS Year, MONTH(createdTime) AS Month, COUNT(*) AS TotalStores " +
     "FROM Stores " +
     "GROUP BY YEAR(createdTime), MONTH(createdTime) " +
     "ORDER BY YEAR(createdTime), MONTH(createdTime)", nativeQuery = true)
List<Map<String, Object>> countStoresByMonth();

// top 5 của hàng bán chạy nhất
@Query(value = "SELECT TOP 5 " +
    "COALESCE(SUM(od.quantity), 0) AS totalOrders, " +
    "s.nameStore AS StoreName, " +
    "s.imgBackgound AS ImgBackgound, " +
    "COALESCE(SUM(od.price * od.quantity * (1 + pc.vat)), 0) AS totalRevenue, " +
    "s.id AS idImageStore, " +
    "MAX(i.imageName) AS ImageNameStore, " + // Chọn hình ảnh đại diện
    "COALESCE(MAX(pc.vat), 0) AS vat " + // Gộp VAT
    "FROM Stores s " +
    "LEFT JOIN Orders o ON s.id = o.StoreId " +
    "LEFT JOIN OrderDetails od ON o.id = od.orderId " +
    "LEFT JOIN Products p ON od.productDetailId = p.id " + // Sử dụng productDetailId
    "LEFT JOIN ProductCategorys pc ON p.categoryId = pc.id " +
    "LEFT JOIN Images i ON s.id = i.productId " +
    "WHERE o.orderStatus = 'Hoàn thành' " +
    "GROUP BY s.id, s.nameStore, s.imgBackgound, s.id " + // Nhóm theo idImageStore
    "ORDER BY totalRevenue DESC", 
    nativeQuery = true)
List<Map<String, Object>> findTop5StoresByOrdersAndRevenue();



// Doanh thu 
@Query(value = "SELECT " +
    "YEAR(o.paymentDate) AS Year, " +
    "MONTH(o.paymentDate) AS Month, " +
    "SUM(od.quantity * od.price * (1 - (pc.vat / 100.0)))  AS TotalRevenue " +
    "FROM Orders o " +
    "JOIN OrderDetails od ON o.id = od.orderId " +
    "JOIN Products p ON od.productDetailId = p.id " +
    "JOIN ProductCategorys pc ON p.categoryId = pc.id " +
    "JOIN Stores s ON o.storeId = s.id " + // Liên kết với bảng Stores
    "WHERE o.paymentDate IS NOT NULL AND o.orderstatus = 'Hoàn thành' " + // Điều kiện đơn hàng đã hoàn thành
    "GROUP BY YEAR(o.paymentDate), MONTH(o.paymentDate) " +
    "ORDER BY YEAR(o.paymentDate), MONTH(o.paymentDate)", 
    nativeQuery = true)
List<Map<String, Object>> findRevenueByYearAndMonthForAllStores();



// Doanh thu theo năm
@Query(value = "SELECT " +
         "YEAR(o.paymentDate) AS [Year], " +
         "SUM(od.quantity * od.price) AS TotalRevenue " +
         "FROM Orders o " +
         "INNER JOIN OrderDetails od ON o.id = od.orderId " +
         "WHERE o.orderStatus = 'Hoàn thành' " +
         "GROUP BY YEAR(o.paymentDate) " +
         "ORDER BY [Year]", 
 nativeQuery = true)
List<Map<String, Object>> findRevenueByYear();


// Doanh thu theo tháng
@Query(value = "SELECT " +
               "DATEPART(YEAR, o.paymentDate) AS [Year], " +
               "DATEPART(MONTH, o.paymentDate) AS [Month], " +
               "SUM(od.quantity * od.price * (1 - (pc.vat / 100.0))) AS TotalRevenue " +
               "FROM Orders o " +
               "INNER JOIN OrderDetails od ON o.id = od.orderId " +
               "INNER JOIN Products p ON od.productDetailId = p.id " +
               "JOIN ProductCategorys pc ON p.categoryId = pc.id " +
               "INNER JOIN Stores s ON p.storeId = s.id " +
               "WHERE o.orderStatus = 'Hoàn thành' " +
               "GROUP BY DATEPART(YEAR, o.paymentDate), DATEPART(MONTH, o.paymentDate) " +
               "ORDER BY [Year], [Month]", 
       nativeQuery = true)
List<Map<String, Object>> findTotalRevenueByMonthAndStore();




// Doanh thu theo ngày
@Query(value = "SELECT " +
         "CAST(o.paymentDate AS DATE) AS OrderDate, " +
         "SUM(od.quantity * od.price) AS TotalRevenue " +
         "FROM Orders o " +
         "INNER JOIN OrderDetails od ON o.id = od.orderId " +
         "WHERE o.orderStatus = 'Hoàn thành' " +
         "GROUP BY CAST(o.paymentDate AS DATE) " +
         "ORDER BY OrderDate", 
 nativeQuery = true)
List<Map<String, Object>> findRevenueByDay();


//Đếm số lượng cửa hàng theo ngày
@Query(value = "SELECT CAST(createdTime AS DATE) AS Date, COUNT(*) AS NumberOfStores " +
     "FROM Stores " +
     "GROUP BY CAST(createdTime AS DATE) " +
     "ORDER BY Date", 
nativeQuery = true)
List<Map<String, Object>> countStoresByDay();

}
