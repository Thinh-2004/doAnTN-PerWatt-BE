package com.duantn.be_project.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.duantn.be_project.model.ProductDetail;

public interface ProductDetailRepository extends JpaRepository<ProductDetail, Integer> {
    // Tìm theo id Product
    @Query("select pd from ProductDetail pd where pd.product.id = ?1")
    List<ProductDetail> findByIdProduct(Integer id);

    @Query("select pd from ProductDetail pd where pd.product.id = ?1")
    List<ProductDetail> findIdProductByIdProduct(Integer idProduct);

    @Query("select pd from ProductDetail pd where pd.quantity = 0 and pd.product.store.id = ?1")
    List<ProductDetail> countDetailProductSoldOut(Integer id);

    @Query("""
            select min(pd.price), max(pd.price) from ProductDetail pd where pd.product.productcategory.name like ?1 or pd.product.trademark.name like ?1
            """)
    List<Object[]> minMaxPriceDetail(String name);

}
