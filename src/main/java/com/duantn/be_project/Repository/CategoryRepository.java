package com.duantn.be_project.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.duantn.be_project.model.ProductCategory;

public interface CategoryRepository extends JpaRepository<ProductCategory, Integer> {
    @Query("select pc from ProductCategory pc order by case when pc.name like 'K%' then 'Z' else pc.name end asc")
    List<ProductCategory> sortByPCAZ();

    // Danh mục sản phẩm cửa trang cửa hàng
    @Query(value = "select DISTINCT B.id,  B.name, B.imageCateProduct, B.vat from products A\r\n" + //
            "inner join ProductCategorys B on A.categoryId = B.id\r\n" + //
            "inner join Stores C on A.storeId = C.id\r\n" + //
            "where A.storeId = ?1", nativeQuery = true)
    List<ProductCategory> cateProductInStore(Integer idStore);

    //Kiểm tra keyWord 
    @Query("select case when count(c) > 0 then true else false end from ProductCategory c where c.name like ?1")
    Boolean checkEmptyCategory(String keyWord);

    //FindAllBy DESC
    @Query("""
            select c from ProductCategory c order by c.id desc
            """)
    List<ProductCategory> findAllByDESC();

    
}
