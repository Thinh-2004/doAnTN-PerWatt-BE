package com.duantn.be_project.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.duantn.be_project.model.Block;

import jakarta.transaction.Transactional;

public interface BlockRepository extends JpaRepository<Block, Integer> {

    // Lấy danh sách block theo idProduct
    @Query("""
            select b from Block b where b.product.id = ?1
            """)
    List<Block> listBlockByIdProduct(Integer idProdcut);

    // Xóa tất cả dữ liệu có idProduct được hủy bỏ ban
    @Transactional
    @Modifying // Tiêm 1 câu truy vấn xóa vào Repository
    // Xóa voucher theo slug
    @Query("""
            DELETE
            FROM Block b

            WHERE b.product.id = ?1
                            """)

    void deleteByIdProduct(Integer idProduct);
}
