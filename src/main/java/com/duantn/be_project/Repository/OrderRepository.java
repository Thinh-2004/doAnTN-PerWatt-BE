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
        @Query(value = "SELECT \r\n" + //
                                "    FORMAT(o.paymentdate, 'MM-yyyy') AS month, \r\n" + //
                                "    SUM(od.quantity * od.price * (1 - pc.vat)) AS revenue, \r\n" + //
                                "    COUNT(o.id) AS orders \r\n" + //
                                "FROM \r\n" + //
                                "    Orders o \r\n" + //
                                "JOIN \r\n" + //
                                "    OrderDetails od ON o.id = od.orderId \r\n" + //
                                "JOIN \r\n" + //
                                "    ProductDetails pd ON od.productdetailid = pd.id \r\n" + //
                                "JOIN \r\n" + //
                                "\tProducts p on p.id = pd.idProduct\r\n" + //
                                "JOIN \r\n" + //
                                "    ProductCategorys pc ON p.categoryId = pc.id \r\n" + //
                                "WHERE \r\n" + //
                                "    o.storeId = :storeId\r\n" + //
                                "    AND o.orderStatus = 'Hoàn thành' \r\n" + //
                                "GROUP BY \r\n" + //
                                "    FORMAT(o.paymentdate, 'MM-yyyy') \r\n" + //
                                "ORDER BY \r\n" + //
                                "    month;\r\n" + //
                                "", nativeQuery = true)
        List<Map<String, Object>> findRevenueByMonth(@Param("storeId") Integer storeId);

}