package com.duantn.be_project.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.duantn.be_project.model.CartItem;

public interface CartRepository extends JpaRepository<CartItem, Integer> {
    @Query("select od from CartItem od where od.user.id = ?1")
    List<CartItem> findAllCartItemlByIdUser(Integer idUser);

}