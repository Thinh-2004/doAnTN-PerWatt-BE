package com.duantn.be_project.Repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.duantn.be_project.model.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

        @Query("select p from Order p where p.user.id = ?1 ")
        List<Order> findAllByUserId(Integer idUser);

        @Query("select p from Order p where p.store.id = ?1 ")
        List<Order> findAllByStoreId(Integer idStore);

        // Số lượng sản phẩm đã bán theo Order hoàn thành
        @Query(value = "SELECT count(*) FROM Orders a " +
                        "INNER JOIN Stores b ON a.store_id = b.id " +
                        "INNER JOIN Products c ON c.store_id = b.id " +
                        "WHERE a.order_status = 'Hoàn thành' AND c.id = ?1", nativeQuery = true)
        public Long countBuyed(Integer idProduct);

        ///// tính số lượng hóa đơn của sản phẩm
        @Query(value = "select count(a.id) from Orders a\r\n" + //
                        "inner join OrderDetails b on a.id = b.orderId\r\n" + //
                        "where a.orderStatus like N'Hoàn Thành' and b.productDetailId = ?1", nativeQuery = true)
        public Integer countOrderBuyed(Integer idProductDetail);

        // biểu đồ doanh thu tháng
        @Query(value = "SELECT " +
                        "FORMAT(o.paymentdate, 'yyyy-MM-dd') AS month, " +
                        "SUM(od.quantity * od.price * (1 - pc.vat)) AS revenue, " +
                        "COUNT(o.id) AS orders, " +
                        "p.name AS productName, " +
                        "pd.nameDetail AS productDetailName, " +
                        "p.id AS productId, " +
                        "pd.id AS productDetailId, " +
                        "od.id AS orderDetailId, " +
                        "SUM(od.quantity) AS totalQuantity, " +
                        "SUM(od.quantity * od.price) AS totalProductRevenue " +
                        "FROM Orders o " +
                        "JOIN OrderDetails od ON o.id = od.orderId  " +
                        "JOIN ProductDetails pd ON od.productdetailid = pd.id " +
                        "JOIN Products p ON pd.idProduct = p.id " +
                        "JOIN ProductCategorys pc ON p.categoryId = pc.id " +
                        "WHERE o.storeId = :storeId " +
                        "AND o.orderStatus = 'Hoàn thành' " +
                        "AND (:startDate IS NULL OR o.paymentdate >= :startDate) " +
                        "AND (:endDate IS NULL OR o.paymentdate <= :endDate) " +
                        "GROUP BY FORMAT(o.paymentdate, 'yyyy-MM-dd'), p.name, pd.nameDetail, p.id, pd.id, od.id", nativeQuery = true)
        List<Map<String, Object>> findRevenueByMonthWithProducts(
                        @Param("storeId") Integer storeId,
                        @Param("startDate") String startDate,
                        @Param("endDate") String endDate);

        // Tính số lượng hóa đơn của sản phẩm gốc theo người dùng
        @Query(value = "select count(DISTINCT o.id) " +
                        "from orders o " +
                        "join orderdetails od on o.id = od.orderId " +
                        "join ProductDetails pd on od.productDetailId = pd.id " +
                        "where o.orderStatus like N'Hoàn Thành' and userId = ?1 and idProduct = ?2", nativeQuery = true)
        public Integer countOrderBuyedOfProductByUser(Integer userId, Integer idProduct);

}