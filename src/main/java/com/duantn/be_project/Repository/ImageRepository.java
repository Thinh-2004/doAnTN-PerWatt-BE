package com.duantn.be_project.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.duantn.be_project.model.Image;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    @Query("select i from Image i where i.product.id = ?1")
    List<Image> findAllByIdProduct(Integer id);
}
