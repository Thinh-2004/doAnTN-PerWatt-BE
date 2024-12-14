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

    // Danh sách các cửa hàng bị ban
    @Query("""
            select s from Store s where s.block = true
            """)
    List<Store> listAllStoreByBan();

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
                YEAR(o.paymentdate) AS Year,
                s.namestore AS StoreName,
                s.slug AS slugStore,
                SUM(od.quantity * od.price * (1 - pc.vat) *
                    CASE
                        WHEN o.idVoucher IS NOT NULL THEN
                            1 - ISNULL(v.discountPrice, 0) / 100.0  -- Áp dụng discountPrice từ bảng Vouchers cho từng sản phẩm
                        ELSE 1
                    END
                ) AS NetRevenue,
                COUNT(DISTINCT o.id) AS OrderCount, -- Đếm số lượng đơn hàng duy nhất
                o.idVoucherAdmin,  -- Thêm cột idVoucherAdmin
                o.idVoucher AS voucherId,  -- Thêm cột idVoucher từ bảng Orders
                v.discountPrice AS discountPrice   -- Thêm cột discountPrice từ bảng Vouchers
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
            LEFT JOIN
                Vouchers v ON o.idVoucher = v.id  -- Kết nối với bảng Vouchers để lấy thông tin discountPrice
            WHERE
                o.orderStatus = 'Hoàn thành'
            GROUP BY
                YEAR(o.paymentdate),  -- Nhóm theo năm
                s.namestore,
                s.slug,
                o.idVoucherAdmin,  -- Nhóm theo idVoucherAdmin để có thể hiển thị
                o.idVoucher,  -- Nhóm theo idVoucher
                v.discountPrice  -- Nhóm theo discountPrice
            ORDER BY
                Year DESC,  -- Sắp xếp theo năm giảm dần
                NetRevenue DESC
            """, nativeQuery = true)
    List<Map<String, Object>> findNetRevenueByStore();

    // Doanh thu theo năm
    @Query(value = "SELECT " +
            "    DATEPART(YEAR, o.paymentDate) AS [Year], " +
            "    SUM(od.quantity * od.price * pc.vat * " +
            "        CASE " +
            "            WHEN o.idVoucher IS NOT NULL THEN " +
            "                1 - ISNULL(v.discountPrice, 0) / 100.0 " + // Áp dụng discountPrice từ bảng Vouchers
            "            ELSE 1 " +
            "        END " +
            "    ) AS TotalRevenue, " + // Tổng thuế đã thu
            "    o.idVoucherAdmin AS VoucherAdmin, " + // Thêm cột idVoucherAdmin
            "    o.idVoucher AS VoucherId, " + // Thêm cột idVoucher
            "    v.discountPrice AS DiscountPrice " + // Thêm cột discountPrice từ bảng Vouchers
            "FROM " +
            "    Orders o " +
            "INNER JOIN " +
            "    OrderDetails od ON o.id = od.orderId " +
            "INNER JOIN " +
            "    ProductDetails pd ON od.productDetailId = pd.id " +
            "INNER JOIN " +
            "    Products p ON p.id = pd.idProduct " +
            "INNER JOIN " +
            "    ProductCategorys pc ON p.categoryId = pc.id " +
            "LEFT JOIN " +
            "    Vouchers v ON o.idVoucher = v.id " + // Kết nối với bảng Vouchers để lấy discountPrice
            "WHERE " +
            "    o.orderStatus = 'Hoàn thành' " +
            "GROUP BY " +
            "    DATEPART(YEAR, o.paymentDate), o.idVoucherAdmin, o.idVoucher, v.discountPrice " +
            "ORDER BY " +
            "    [Year];", nativeQuery = true)
    List<Map<String, Object>> findRevenueByYear();

    // Doanh thu theo tháng
    @Query(value = "SELECT " +
            "    DATEPART(YEAR, o.paymentDate) AS [Year], " +
            "    DATEPART(MONTH, o.paymentDate) AS [Month], " +
            "    s.nameStore AS StoreName, " +
            "    o.idVoucherAdmin AS VoucherAdmin, " + // Thêm cột idVoucherAdmin
            "    o.idVoucher AS VoucherId, " + // Thêm cột idVoucher
            "    v.discountPrice AS DiscountPrice, " + // Thêm cột discountPrice từ bảng Vouchers
            "    SUM( " +
            "         od.quantity * od.price * pc.vat * " +
            "        CASE " +
            "            WHEN o.idVoucher IS NOT NULL THEN " +
            "                1 - ISNULL(v.discountPrice, 0) / 100.0 " + // Áp dụng discountPrice từ bảng Vouchers
            "            ELSE 1 " +
            "        END " +
            "    ) AS TotalVATCollected, " + // Tính tổng doanh thu
            "    od.price AS PriceDetail, " +
            "    od.quantity AS QuantityDetail, " +
            "    pc.vat AS VAT " +
            "FROM Orders o " +
            "INNER JOIN OrderDetails od ON o.id = od.orderId " +
            "INNER JOIN ProductDetails pd ON od.productDetailId = pd.id " +
            "INNER JOIN Products p ON p.id = pd.idProduct " +
            "INNER JOIN ProductCategorys pc ON p.categoryId = pc.id " +
            "INNER JOIN Stores s ON p.storeId = s.id " +
            "LEFT JOIN Vouchers v ON o.idVoucher = v.id " + // Kết nối với bảng Vouchers
            "WHERE o.orderStatus = 'Hoàn thành' " +
            "AND (:startDate IS NULL OR o.paymentDate >= :startDate) " + // Điều kiện cho startDate
            "AND (:endDate IS NULL OR o.paymentDate <= :endDate) " + // Điều kiện cho endDate
            "GROUP BY " +
            "    DATEPART(YEAR, o.paymentDate), " +
            "    DATEPART(MONTH, o.paymentDate), " +
            "    s.nameStore, " +
            "    od.price, " +
            "    od.quantity, " +
            "    pc.vat, " +
            "    o.idVoucherAdmin, " +
            "    o.idVoucher, " +
            "    v.discountPrice " + // Thêm vào GROUP BY để nhóm theo idVoucher và discountPrice
            "ORDER BY " +
            "    [Year], " +
            "    [Month], " +
            "    StoreName", nativeQuery = true)
    List<Map<String, Object>> findTotalVATByMonth(@Param("startDate") String startDate,
            @Param("endDate") String endDate);

    // Doanh thu theo ngày
    @Query(value = "SELECT " +
            "CAST(o.paymentDate AS DATE) AS OrderDate, " +
            "SUM(od.quantity * od.price * pc.vat * " +
            "CASE " +
            "WHEN o.idVoucher IS NOT NULL THEN " +
            "1 - ISNULL(v.discountPrice, 0) / 100.0 " + // Apply discountPrice from Vouchers
            "ELSE 1 " +
            "END) AS TotalRevenue , " +
            "o.idVoucherAdmin AS VoucherAdmin, " + // Add VoucherAdmin
            "o.idVoucher AS VoucherId, " + // Add VoucherId
            "v.discountPrice AS DiscountPrice " + // Add DiscountPrice from Vouchers
            "FROM Orders o " +
            "INNER JOIN OrderDetails od ON o.id = od.orderId " +
            "INNER JOIN ProductDetails pd ON od.productDetailId = pd.id " +
            "INNER JOIN Products p ON p.id = pd.idProduct " +
            "INNER JOIN ProductCategorys pc ON p.categoryId = pc.id " +
            "LEFT JOIN Vouchers v ON o.idVoucher = v.id " + // Join Vouchers table for discountPrice
            "WHERE o.orderStatus = 'Hoàn thành' " +
            "AND (:startDate IS NULL OR o.paymentDate >= :startDate) " +
            "AND (:endDate IS NULL OR o.paymentDate <= :endDate) " +
            "GROUP BY CAST(o.paymentDate AS DATE), o.idVoucherAdmin, o.idVoucher, v.discountPrice " +
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
