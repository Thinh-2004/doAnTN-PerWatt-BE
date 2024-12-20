package com.duantn.be_project.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.duantn.be_project.model.Voucher;
import com.duantn.be_project.model.VoucherDetail;

import jakarta.transaction.Transactional;

public interface VoucherDetailsSellerRepository extends JpaRepository<VoucherDetail, Integer> {

    // Truy vấn kiểm tra xem user đã nhận voucher hay chưa
    @Query("""
            select vd from VoucherDetail vd where vd.user.id = ?1
             """)
    List<VoucherDetail> findAllByIdAUser(Integer id);

    /**
     * Được thực hiện trong một giao dịch (transaction).
     * Nếu có lỗi, toàn bộ giao dịch sẽ bị rollback.
     */
    @Transactional
    @Modifying // Tiêm 1 câu truy vấn xóa vào Repository
    // Xóa voucher theo slug
    @Query("""
            DELETE
            FROM VoucherDetail vd

            WHERE vd.voucher.status = ?1
                            """)

    void deleteByVoucherStatus(String statusVoucher);

    @Transactional
    @Modifying // Tiêm 1 câu truy vấn xóa vào Repository
    // Xóa voucherDetail theo slug
    @Query("""
            DELETE
            FROM VoucherDetail vd

            WHERE vd.voucher.slug = ?1
                            """)

    void deleteByVoucherDetailSlug(String slug);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Vouchers SET quantityVoucher = quantityVoucher - 1 WHERE slug = :slug AND quantityVoucher > 0", nativeQuery = true)
    int updateVoucherQuantity(@Param("slug") String slug);

    @Query("select v from Voucher v where v.id = ?1")
    Voucher slugVoucherById(int voucherId);

}
