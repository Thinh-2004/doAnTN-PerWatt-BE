package com.duantn.be_project.Repository;

import com.duantn.be_project.model.ProductDetail;
import com.duantn.be_project.model.VoucherAdminDetail;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface VoucherAdminDetailRepository extends JpaRepository<VoucherAdminDetail, Integer> {

    // Phương thức tìm danh sách VoucherAdminDetail theo idProductDetail
    @Query("SELECT vad FROM VoucherAdminDetail vad WHERE vad.product.id = :idProduct")
List<VoucherAdminDetail> findByIdProduct(@Param("idProduct") Integer idProduct);


    // xóa tất cả liên quan tới VoucherAdminDetail
    @Transactional
    @Modifying
    @Query("DELETE FROM VoucherAdminDetail vad WHERE vad.voucherAdmin.id = :voucherAdminId")
    void deleteByVoucherAdminId(int voucherAdminId);

    // xóa từng VoucherAdminDetail
    @Transactional
@Modifying
@Query("DELETE FROM VoucherAdminDetail vad WHERE vad.product.id = :idProduct")
void deleteByProduct(@Param("idProduct") Integer idProduct);

    
    List<VoucherAdminDetail> findByVoucherAdminId(Integer voucherAdminId);
    List<VoucherAdminDetail> findAllByVoucherAdminId(Integer voucherAdminId);
     // Lấy tất cả các sản phẩm đã đăng ký
     List<VoucherAdminDetail> findAll();

}
