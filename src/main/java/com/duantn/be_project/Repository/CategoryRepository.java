package com.duantn.be_project.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.duantn.be_project.model.ProductCategory;

public interface CategoryRepository extends JpaRepository<ProductCategory, Integer> {
    @Query("select pc from ProductCategory pc order by case when pc.name like 'K%' then 'Z' else pc.name end asc")
    List<ProductCategory> sortByPCAZ();
}
