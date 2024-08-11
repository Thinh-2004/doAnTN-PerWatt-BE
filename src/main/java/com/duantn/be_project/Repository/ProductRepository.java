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

}
