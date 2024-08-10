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
}