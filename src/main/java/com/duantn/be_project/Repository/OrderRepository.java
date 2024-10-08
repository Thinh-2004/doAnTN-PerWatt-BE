package com.duantn.be_project.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

}