package com.duantn.be_project.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import com.duantn.be_project.model.VoucherAdmin;
import com.duantn.be_project.model.VoucherAdminDetail;
public interface VouchersAdminRepository extends JpaRepository<VoucherAdmin, Integer> {
    // Truy vấn lấy discountPrice theo id của ProductDetail
    @Query("SELECT v.discountprice FROM VoucherAdminDetail v WHERE v.productDetail.id = ?1")
    List<Float> findDiscountPriceByProductDetailId(Integer productDetailId);

    // Bạn cũng có thể lấy toàn bộ VoucherAdminDetail dựa theo ProductDetail
    @Query("SELECT v FROM VoucherAdminDetail v WHERE v.productDetail.id = ?1")
    List<VoucherAdminDetail> findByProductDetailId(Integer productDetailId);
}
