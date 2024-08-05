package com.duantn.be_project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duantn.be_project.model.ProductCategory;

public interface CategoryRepository extends JpaRepository<ProductCategory, Integer> {
    
}
