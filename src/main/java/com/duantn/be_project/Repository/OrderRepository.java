package com.duantn.be_project.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.duantn.be_project.model.Order;
import com.duantn.be_project.model.Store;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("select p from Order p where p.user.id = ?1 ")
    List<Order> findAllByUserId(Integer idUser);

    @Query("select p from Order p where p.store.id = ?1 ")
    List<Order> findAllByStoreId(Integer idStore);

    Optional<Store> findStoreById(Integer storeId);
}