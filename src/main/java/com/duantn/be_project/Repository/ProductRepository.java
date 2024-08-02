package com.duantn.be_project.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.duantn.be_project.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("select p from Product p where p.store.id = ?1 ")
    List<Product> findAllByStoreId(Integer idStore);
}
