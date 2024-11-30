package com.duantn.be_project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // all sản phẩm của all cửa hàng
    @Query(value = """
                             WITH ProductSales AS (
                SELECT
                    p.storeId AS storeId,
                    p.id AS productId,
                    -- Lấy id từ ProductDetails nếu có, nếu không thì lấy từ Products
                    pd.id AS productDetailId,  -- Lấy productDetailId từ bảng ProductDetails
                    pd.nameDetail AS name,  -- Lấy tên sản phẩm từ bảng ProductDetails nếu có, nếu không lấy từ bảng Products
                    pd.imageDetail AS imgSrc,  -- Lấy hình ảnh từ bảng ProductDetails nếu có, nếu không lấy từ Images
                    COALESCE(SUM(od.quantity), 0) AS sold,
                    MAX(pd.price) AS price,
                    p.name AS productName,  -- Thêm tên sản phẩm từ bảng Products
            		p.slug as slugProduct,
                    (SELECT TOP 1 i.imagename FROM Images i WHERE i.productid = p.id) AS productImage  -- Thêm hình ảnh từ bảng Images
                FROM
                    Products p
                LEFT JOIN
                    ProductDetails pd ON p.id = pd.idProduct
                LEFT JOIN
                    OrderDetails od ON pd.id = od.productDetailId
                LEFT JOIN
                    Orders o ON od.orderId = o.id
                WHERE
                    o.orderStatus = 'Hoàn thành'
                    AND od.quantity IS NOT NULL
                GROUP BY
                    p.storeId, p.id, p.name, pd.id, pd.nameDetail, pd.imageDetail, p.slug
            )
            SELECT
                s.nameStore AS StoreName,
                ps.productId AS ProductID,
                ps.productDetailId AS ProductDetailID,
                ps.name AS ProductNameDetail,
                ps.price AS ProductPriceDetail,
                ps.sold AS QuantitySoldDetail,
                ps.imgSrc AS ImageNameDetail,
                ps.productName AS ProductName,  -- Thêm cột tên sản phẩm
                ps.productImage AS ProductImage, -- Thêm cột hình ảnh sản phẩm
            	ps.slugProduct as slugProduct,
            	s.slug as slugStore
            FROM
                ProductSales ps
            JOIN
                Stores s ON ps.storeId = s.id
            ORDER BY
                s.nameStore ASC, ps.sold DESC;
                                    """, nativeQuery = true)
    List<Map<String, Object>> findProductSalesByStore();

    // Doanh thu từng cửa hàng
    @Query(value = """
                        SELECT
                s.namestore AS StoreName,
            	 s.slug AS slugStore,
                SUM(od.quantity * od.price * (1 - pc.vat)) AS NetRevenue
            FROM
                Stores s
            JOIN
                Products p ON s.id = p.storeId
            JOIN
                ProductCategorys pc ON p.categoryId = pc.id
            JOIN
                ProductDetails pd ON pd.idProduct = p.id
            JOIN
                OrderDetails od ON pd.id = od.productDetailId
            JOIN
                Orders o ON od.orderId = o.id
            WHERE
                o.orderStatus = 'Hoàn thành' -- Chỉ tính những đơn hàng đã hoàn thành
            GROUP BY
                s.namestore -- Nhóm theo cửa hàng
            	,s.slug
            ORDER BY
                NetRevenue DESC; -- Sắp xếp theo doanh thu sau thuế giảm dần

                        """, nativeQuery = true)
    List<Map<String, Object>> findNetRevenueByStore();

    // Doanh thu theo năm
    @Query(value = "SELECT \n" + //
            "    DATEPART(YEAR, o.paymentDate) AS [Year],\n" + //
            "    SUM(od.quantity * od.price * pc.vat) AS TotalRevenue\n" + //
            "FROM \n" + //
            "    Orders o\n" + //
            "INNER JOIN \n" + //
            "    OrderDetails od ON o.id = od.orderId\n" + //
            "INNER JOIN \n" + //
            "    ProductDetails pd ON od.productDetailId = pd.id\n" + //
            "INNER JOIN \n" + //
            "\tProducts p on p.id = pd.idProduct\n" + //
            "INNER JOIN \n" + //
            "    ProductCategorys pc ON p.categoryId = pc.id\n" + //
            "WHERE \n" + //
            "    o.orderStatus = 'Hoàn thành'\n" + //
            "GROUP BY \n" + //
            "    DATEPART(YEAR, o.paymentDate)\n" + //
            "ORDER BY \n" + //
            "    [Year];", nativeQuery = true)
    List<Map<String, Object>> findRevenueByYear();

    // Doanh thu theo tháng
    @Query(value = "SELECT " +
            "DATEPART(YEAR, o.paymentDate) AS [Year], " +
            "DATEPART(MONTH, o.paymentDate) AS [Month], " +
            "s.nameStore AS StoreName, " +
            "SUM(od.quantity * od.price * pc.vat) AS TotalVATCollected, " +
            "od.price AS PriceDetail, " +
            "od.quantity AS QuantityDetail, " +
            "pc.vat AS VAT " +
            "FROM Orders o " +
            "INNER JOIN OrderDetails od ON o.id = od.orderId " +
            "INNER JOIN ProductDetails pd ON od.productDetailId = pd.id " +
            "INNER JOIN Products p ON p.id = pd.idProduct " +
            "INNER JOIN ProductCategorys pc ON p.categoryId = pc.id " +
            "INNER JOIN Stores s ON p.storeId = s.id " +
            "WHERE o.orderStatus = 'Hoàn thành' " +
            "AND (o.paymentDate >= :startDate OR :startDate IS NULL) " +
            "AND (o.paymentDate <= :endDate OR :endDate IS NULL) " +
            "GROUP BY DATEPART(YEAR, o.paymentDate), DATEPART(MONTH, o.paymentDate), s.nameStore, od.price, od.quantity, pc.vat "
            +
            "ORDER BY [Year], [Month], StoreName", nativeQuery = true)
    List<Map<String, Object>> findTotalVATByMonth(@Param("startDate") String startDate,
            @Param("endDate") String endDate);

    // Doanh thu theo ngày
    @Query(value = "SELECT " +
            "CAST(o.paymentDate AS DATE) AS OrderDate, " +
            "SUM(od.quantity * od.price * pc.vat) AS TotalRevenue " +
            "FROM Orders o " +
            "INNER JOIN OrderDetails od ON o.id = od.orderId " +
            "INNER JOIN ProductDetails pd ON od.productDetailId = pd.id " +
            "INNER JOIN Products p ON p.id = pd.idProduct " +
            "INNER JOIN ProductCategorys pc ON p.categoryId = pc.id " +
            "WHERE o.orderStatus = 'Hoàn thành' " +
            "AND (:startDate IS NULL OR CAST(o.paymentDate AS DATE) >= :startDate) " +
            "AND (:endDate IS NULL OR CAST(o.paymentDate AS DATE) <= :endDate) " +
            "GROUP BY CAST(o.paymentDate AS DATE) " +
            "ORDER BY OrderDate", nativeQuery = true)
    List<Map<String, Object>> findRevenueByDay(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    // Đếm sô lượng cửa hàng được tạo theo tháng
    @Query(value = "SELECT YEAR(createdTime) AS Year, MONTH(createdTime) AS Month, COUNT(*) AS TotalStores " +
            "FROM Stores " +
            "GROUP BY YEAR(createdTime), MONTH(createdTime) " +
            "ORDER BY YEAR(createdTime), MONTH(createdTime)", nativeQuery = true)
    List<Map<String, Object>> countStoresByMonth();

    // Đếm số lượng cửa hàng theo ngày
    @Query(value = "SELECT CAST(createdTime AS DATE) AS Date, COUNT(*) AS NumberOfStores " +
            "FROM Stores " +
            "GROUP BY CAST(createdTime AS DATE) " +
            "ORDER BY Date", nativeQuery = true)
    List<Map<String, Object>> countStoresByDay();

}
