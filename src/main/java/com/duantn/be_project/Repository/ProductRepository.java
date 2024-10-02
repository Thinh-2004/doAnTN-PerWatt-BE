package com.duantn.be_project.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.duantn.be_project.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("select p from Product p where p.store.id = ?1 ")
    List<Product> findAllByStoreId(Integer idStore);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.store.id = :idStore")
    List<Product> findAllByStoreIdWithImages(@Param("idStore") Integer idStore);

    @Query("select p from Product p order by p.id desc")
    List<Product> findAllDesc();

    //khai
    //top sản phẩm bán chạy
    @Query(value = "WITH RankedImages AS (" +
    "    SELECT " +
    "        p.id AS ProductId, " +
    "        p.name AS ProductName, " +
    "        i.id AS ImageId, " + // Lấy id của ảnh
    "        i.imageName AS imageName, " +
    "        COALESCE(SUM(od.quantity * od.price), 0) AS totalRevenue, " + // Tính tổng doanh thu từ giá và số lượng trong OrderDetails
    "        COALESCE(SUM(od.quantity), 0) AS totalQuantitySold, " +
    "        ROW_NUMBER() OVER (PARTITION BY p.id ORDER BY i.imageName) AS rn " +
    "    FROM Products p " +
    "    LEFT JOIN OrderDetails od ON p.id = od.productDetailId " + 
    "    LEFT JOIN Orders o ON od.orderId = o.id " +
    "    LEFT JOIN Images i ON p.id = i.productId " +
    "    WHERE o.orderStatus = 'Hoàn thành' " +
    "    GROUP BY p.id, p.name, i.id, i.imageName " + // Nhóm theo các trường cần thiết
    ") " +
    "SELECT " +
    "    ProductId, " + 
    "    ProductName, " +
    "    ImageId, " + 
    "    imageName, " +
    "    totalRevenue, " +
    "    totalQuantitySold " +
    "FROM RankedImages " +
    "ORDER BY totalRevenue DESC", nativeQuery = true)
List<Object[]> findTopSellingProducts();






@Query(value = "SELECT " +
    "    p.name AS nameProduct, " +
    "    pd.imageDetail AS image, " +
    "    pd.price, " +
    "    pd.quantity AS quantityRemaining, " +
    "    COALESCE(SUM(od.quantity), 0) AS quantitySold, " +
    "    pc.name AS nameCategory, " +
    "    COALESCE(MAX(v.discountPrice), 0) AS discount " +
    "FROM " +
    "    Products p " +
    "JOIN " +
    "    ProductDetails pd ON p.id = pd.idProduct " +
    "LEFT JOIN " +
    "    OrderDetails od ON p.id = od.productId " +
    "JOIN " +
    "    ProductCategorys pc ON p.categoryId = pc.id " +
    "LEFT JOIN " +
    "    VoucherAdminDetail vad ON p.id = vad.idProduct " +
    "LEFT JOIN " +
    "    Vouchers v ON vad.idVoucherAdmin = v.id " +
    "GROUP BY " +
    "    p.name, pd.imageDetail, pd.price, pd.quantity, pc.name " +
    "ORDER BY " +
    "    p.name", nativeQuery = true)
List<Object[]> findAllProductDetails();


@Query(value = "SELECT " +
    "    p.name AS nameProduct, " +
    "    pd.imageDetail AS image, " +
    "    pd.price, " +
    "    pd.quantity AS quantityRemaining, " +
    "    COALESCE(SUM(od.quantity), 0) AS quantitySold, " +
    "    pc.name AS nameCategory, " +
    "    COALESCE(MAX(v.discountPrice), 0) AS discount " +
    "FROM " +
    "    Products p " +
    "JOIN " +
    "    ProductDetails pd ON p.id = pd.idProduct " +
    "LEFT JOIN " +
    "    OrderDetails od ON p.id = od.productId " +
    "JOIN " +
    "    ProductCategorys pc ON p.categoryId = pc.id " +
    "LEFT JOIN " +
    "    VoucherAdminDetail vad ON p.id = vad.idProduct " +
    "LEFT JOIN " +
    "    Vouchers v ON vad.idVoucherAdmin = v.id " +
    "WHERE " +
    "    p.id = :id " +
    "GROUP BY " +
    "    p.name, pd.imageDetail, pd.price, pd.quantity, pc.name " +
    "ORDER BY " +
    "    p.name", nativeQuery = true)
Object[] findProductDetailsById(@Param("id") Integer id);
// top 10 san pham ban chay seller
@Query(value = "WITH ProductSales AS (" +
               "    SELECT " +
               "        p.name AS name, " +
               "        COALESCE(SUM(od.quantity), 0) AS sold, " +
               "        MAX(pd.price) AS price, " +
               "        MAX(i.id) AS imgId, " +  // Lấy ID của hình ảnh lớn nhất nếu có nhiều ảnh
               "        MAX(i.imageName) AS imgSrc " +  // Lấy ảnh có tên nhỏ nhất theo sản phẩm
               "    FROM " +
               "        Products p " +
               "    INNER JOIN " +
               "        ProductDetails pd ON p.id = pd.idProduct " +
               "    LEFT JOIN " +
               "        OrderDetails od ON pd.id = od.productDetailId " +  
               "    LEFT JOIN " +
               "        Orders o ON od.orderId = o.id " +  // Thêm join với bảng Orders để lọc trạng thái
               "    LEFT JOIN " +
               "        Images i ON pd.id = i.productid AND i.productid = pd.idProduct " +        
               "    WHERE " +
               "        p.storeId = :storeId " +
               "        AND o.orderStatus = 'Hoàn thành' " +  // Lọc theo trạng thái 'Hoàn thành'
               "    GROUP BY " +
               "        p.name " +  // Nhóm theo tên sản phẩm để lấy ảnh đại diện
               ") " +
               "SELECT TOP 10 " +
               "    name, " +
               "    price, " +
               "    sold, " +
               "    imgId AS idImage, " +
               "    imgSrc AS imageName " +
               "FROM " +
               "    ProductSales " +
               "ORDER BY " +
               "    sold DESC", nativeQuery = true)
List<Object[]> findTopSellingProductsByStoreId(@Param("storeId") Integer storeId);


@Query(value = "SELECT " +
"    CAST(o.paymentDate AS DATE) AS date, " +
"    SUM(od.price * od.quantity) AS revenue " +
"FROM " +
"    Orders o " +
"JOIN " +
"    OrderDetails od ON o.id = od.orderId " +
"WHERE " +
"    o.storeId = :storeId " +
"    AND o.orderStatus = 'hoàn thành' " +
"GROUP BY " +
"    CAST(o.paymentDate AS DATE) " +
"ORDER BY " +
"    date", nativeQuery = true)
List<Object[]> findRevenueByStoreId(@Param("storeId") Integer storeId);


}
