package com.duantn.be_project.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.duantn.be_project.model.Voucher;

import jakarta.transaction.Transactional;

public interface VoucherSellerRepository extends JpaRepository<Voucher, Integer> {
    // Lấy các namevoucher duy nhất để phân trang

    // fill all voucher by id store
    // @Query("""
    // select v from Voucher v where v.productDetail.product.store.id = ?1 and
    // ((v.vouchername like ?2 or v.productDetail.product.name like ?2) and v.status
    // like ?3 )
    // """)
    // Page<Voucher> findAllByIdStore(Integer idStore, String voucherName, String
    // status, Pageable pageable);
    @Query("""
                select v
                from Voucher v
                where v.productDetail.product.store.id = ?1
                and ((v.vouchername like ?2 or v.productDetail.product.name like ?2) and v.status like ?3)
                order by 
                case
                when ?4 = "newVouchers" then -v.id 
                when ?4 = "oldVouchers" then v.id
                when ?4 = "disCountPriceASC" then v.discountprice
                when ?4 = "disCountPriceDESC" then -v.discountprice
                else -v.id
                end
            """)
    List<Voucher> findAllByIdStore(Integer idStore, String voucherName, String status, String sortBy);

    // Lấy tất cả các voucher theo idStore và vouchername
    @Query("""
            select v
            from Voucher v
            where v.productDetail.product.store.id = ?1
            and v.vouchername = ?2
            """)
    List<Voucher> findByIdStoreAndNameVoucher(Integer idStore, String namevoucher);

    // fill all discountPrice by idProduct
    @Query("""
            select v from Voucher v where v.productDetail.product.id = ?1 order by v.id desc
            """)
    List<Voucher> findAllByIdProduct(Integer idStore);

    @Query("""
            select v from Voucher v where v.productDetail.product.store.slug like ?1 order by v.id desc
            """)
    List<Voucher> findAllBySlugStore(String slug);

    // Truy vấn trùng mã
    @Query(value = "select count(*) from Vouchers a\r\n" + //
            "inner join ProductDetails b on a.idProductDetail = b.id\r\n" + //
            "where voucherName like ?1 or b.id = ?2", nativeQuery = true)
    Integer checkTrungNameVoucherAndIdProductDetail(String name, Integer idProductDetail);

    // Edit by voucherName
    @Query("""
            select v from Voucher v where v.slug like ?1
            """)
    List<Voucher> editVoucherBySlug(String slug);

    // tìm kiếm theo tên để update
    @Query("""
            select v from Voucher v where v.slug like ?1
            """)
    List<Voucher> findBySlug(String slug);

    // Kiểm tra sự tồn tại của slug
    public Boolean existsBySlug(String slug);

    /**
     * Được thực hiện trong một giao dịch (transaction).
     * Nếu có lỗi, toàn bộ giao dịch sẽ bị rollback.
     */
    @Transactional
    @Modifying // Tiêm 1 câu truy vấn xóa vào Repository
    // Xóa voucher theo slug
    @Query("DELETE FROM Voucher v WHERE v.slug = ?1")
    void deleteBySlug(String slug);

    // Truy vấn tìm danh sách voucher theo vouchername
    @Query("""
            select v from Voucher v where v.vouchername like ?1
                        """)
    List<Voucher> findByVoucherName(String voucherName);

    // Kiểm tra voucher có bị trùng khi cập nhật hay không
    @Query("""
            select v from Voucher v where v.vouchername like ?1
            """)
    List<Voucher> checkTrungVoucher(String voucherName);
}
