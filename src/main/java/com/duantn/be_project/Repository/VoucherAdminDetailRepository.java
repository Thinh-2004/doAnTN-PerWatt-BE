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

@Repository
public interface VoucherAdminDetailRepository extends JpaRepository<VoucherAdminDetail, Integer> {

    // Custom query để tìm VoucherAdminDetail theo ProductDetail
    @Query("SELECT vad FROM VoucherAdminDetail vad WHERE vad.productDetail = :productDetail")
    VoucherAdminDetail findByProductDetail(@Param("productDetail") ProductDetail productDetail);

    // Phương thức tìm danh sách VoucherAdminDetail dựa trên VoucherAdmin ID
    List<VoucherAdminDetail> findByVoucherAdminId(Integer voucherAdminId);

    
//14/10
   // Phương thức tìm danh sách VoucherAdminDetail theo idProductDetail
   @Query("SELECT vad FROM VoucherAdminDetail vad WHERE vad.productDetail.id = :idProductDetail")
   List<VoucherAdminDetail> findByIdProductDetail(@Param("idProductDetail") Integer idProductDetail);
    
   //xóa tất cả liên quan tới VoucherAdminDetail
//    @Transactional
   @Modifying
    @Query("DELETE FROM VoucherAdminDetail vad WHERE vad.voucherAdmin.id = :voucherAdminId")
    void deleteByVoucherAdminId(int voucherAdminId);

    //xóa từng VoucherAdminDetail
    // @Transactional
    @Modifying
    @Query("DELETE FROM VoucherAdminDetail vad WHERE vad.productDetail.id = :idProductDetail")
    void deleteByIdProductDetail(@Param("idProductDetail") Integer idProductDetail);

    // Cập nhật discountPrice cho VoucherAdminDetail theo idVoucherAdminDetail
    @Transactional
    @Modifying
    @Query("UPDATE VoucherAdminDetail v SET v.discountprice = :discountPrice WHERE v.id = :id")
    void updateDiscountPrice(@Param("id") Integer id, @Param("discountPrice") Float discountPrice);
    

 

   
}
