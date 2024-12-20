package com.duantn.be_project.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.duantn.be_project.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("select p from Product p where p.store.id = ?1 order by p.id desc ")
    List<Product> findAllByStoreId(Integer idStore);

    // checkBanProudctByIdStore
    @Query("""
            select count(p) from Product p where p.store.id = ?1 and p.block = true
            """)
    Integer checkBan(Integer idStore);

    // Show product in store by manage seller
    @Query("""
            SELECT p, MAX(pd.price) as maxPrice, COUNT(o.orderstatus) as orderCount, SUM(pd.quantity) as quantityCount

            FROM Product p
            JOIN p.productDetails pd
            LEFT JOIN OrderDetail od ON pd.id = od.productDetail.id
            LEFT JOIN Order o ON od.order.id = o.id AND o.orderstatus LIKE 'Hoàn Thành'
            WHERE p.store.slug LIKE ?1
            AND ((p.productcategory.name like ?2 or p.productcategory.name like ?3 or p.productcategory.name like ?4 or p.productcategory.name like ?5 or p.productcategory.name like ?6)
                          or p.productcategory.id = ?7 or (p.trademark.name like ?2 or  p.trademark.name like ?3 or p.trademark.name like ?4 or p.trademark.name like ?5 or p.trademark.name like ?6))
            GROUP BY p
            HAVING (SUM(pd.quantity) = 0 OR ?8 = false)
            """)
    Page<Object[]> findAllByStoreIdWithSlugStore(String slugStore, String name1, String name2, String name3,
            String name4, String name5, Integer idCate, boolean quantityFilter,
            Pageable pageable);

    // Show product in store by home store
    @Query("""
            SELECT p, MAX(pd.price) as maxPrice, COUNT(o.orderstatus) as orderCount, SUM(pd.quantity) as quantityCount

            FROM Product p
            JOIN p.productDetails pd
            LEFT JOIN OrderDetail od ON pd.id = od.productDetail.id
            LEFT JOIN Order o ON od.order.id = o.id AND o.orderstatus LIKE 'Hoàn Thành'
            WHERE p.store.slug LIKE ?1 AND (p.block = false and p.store.block = false)
            AND ((p.productcategory.name like ?2 or p.productcategory.name like ?3 or p.productcategory.name like ?4 or p.productcategory.name like ?5 or p.productcategory.name like ?6)
                          or p.productcategory.id = ?7 or (p.trademark.name like ?2 or  p.trademark.name like ?3 or p.trademark.name like ?4 or p.trademark.name like ?5 or p.trademark.name like ?6))
            GROUP BY p
            HAVING (SUM(pd.quantity) = 0 OR ?8 = false)
            """)
    Page<Object[]> showAllProductInStore(String slugStore, String name1, String name2, String name3,
            String name4, String name5, Integer idCate, boolean quantityFilter,
            Pageable pageable);

    @Query("select p from Product p order by p.id desc")
    List<Product> findAllDesc();

    @Query("select p from Product p where p.store.id = ?1")
    List<Product> CountProductByIdStore(Integer id);

    Optional<Product> findBySlug(String slug); // Thêm phương thức tìm theo slug

    // Phương thức kiểm tra tồn tại bằng slug
    boolean existsBySlug(String slug);

    // Find by namePr, nameCate, nameTrademark (home perwatt)
    @Query("""
                SELECT p, COUNT(o.orderstatus) AS orderCount
                FROM Product p
                JOIN p.productDetails pd
                LEFT JOIN OrderDetail od ON pd.id = od.productDetail.id
                LEFT JOIN Order o ON od.order.id = o.id AND o.orderstatus LIKE 'Hoàn thành'
                WHERE (p.block = false and p.store.block = false)
                  AND (
                       p.name LIKE ?1 OR p.name LIKE ?2 OR p.name LIKE ?3 OR p.name LIKE ?4 OR p.name LIKE ?5
                       OR p.productcategory.name LIKE ?1 OR p.productcategory.name LIKE ?2 OR p.productcategory.name LIKE ?3 OR p.productcategory.name LIKE ?4 OR p.productcategory.name LIKE ?5
                       OR p.productcategory.id = ?6
                       OR p.trademark.name LIKE ?1 OR p.trademark.name LIKE ?2 OR p.trademark.name LIKE ?3 OR p.trademark.name LIKE ?4 OR p.trademark.name LIKE ?5
                  )
                GROUP BY p
            """)
    Page<Object[]> findByNamePrCateTrademark(String name1, String name2, String name3, String name4, String name5,
            Integer idCate, Pageable pageable);

    // Truy vấn sản phẩm liên quan (bao gồm taxcode & noTaxCode)
    @Query("""
                SELECT p,
                       MAX(pd.price) AS maxPrice,
                       COUNT(o.orderstatus) AS orderCount
                FROM Product p
                JOIN p.productDetails pd
                LEFT JOIN OrderDetail od ON pd.id = od.productDetail.id
                LEFT JOIN Order o ON od.order.id = o.id
                AND o.orderstatus LIKE 'Hoàn thành'
                WHERE (p.block = false and p.store.block = false) AND  (p.productcategory.name = ?1 OR p.trademark.name = ?1)
                  AND  ((p.name like ?2 or p.name like ?3 or p.name like ?4 or p.name like ?5 or p.name like ?6 )
                   or (p.productcategory.name like ?2 or p.productcategory.name like ?3 or p.productcategory.name like ?4 or p.productcategory.name like ?5 or p.productcategory.name like ?6)
                   or (p.trademark.name like ?2 or  p.trademark.name like ?3 or p.trademark.name like ?4 or p.trademark.name like ?5 or p.trademark.name like ?6))
                  AND pd.price BETWEEN ?7 AND ?8
                  AND ((?9 is null or 1 IN (?9) AND p.store.taxcode IS NOT NULL)
                  OR (?9 is null or 2 IN (?9) AND p.store.taxcode IS NULL))
                  AND (?10 IS NULL OR (p.trademark.name IN (?10)))
                GROUP BY p
            """)
    Page<Object[]> queryFindMore(String nameUrl, String name1, String name2, String name3, String name4,
            String name5,
            Integer minPrice, Integer maxPrice,
            List<Integer> shopType, List<String> tradeMark, Pageable pageable);

    // Truy vấn sản phẩm có taxcode full danh sách
    @Query("""
            SELECT p,
                      MAX(pd.price) AS maxPrice,
                      COUNT(o.orderstatus) AS orderCount
               FROM Product p
               JOIN p.productDetails pd
               LEFT JOIN OrderDetail od ON pd.id = od.productDetail.id
               LEFT JOIN Order o ON od.order.id = o.id
               AND o.orderstatus LIKE 'Hoàn thành'
               WHERE (p.block = false and p.store.block = false) AND (p.productcategory.name = ?1 OR p.trademark.name = ?1)
                 AND (p.name LIKE ?2)
                 AND pd.price BETWEEN ?3 AND ?4
                 AND ((?5 is null or 1 IN (?5) AND p.store.taxcode IS NOT NULL)
                 OR (?5 is null or 2 IN (?5) AND p.store.taxcode IS NULL))
                 AND (?6 IS NULL OR (p.trademark.name IN (?6)))
               GROUP BY p
                        """)
    List<Object[]> queryFindMoreFullList(String nameUrl, String name, Integer minPrice, Integer maxPrice,
            List<Integer> shopType, List<String> tradeMark, Sort sort);

    // Danh sách sản phẩm có taxcode
    @Query("""
            select p,  MAX(pd.price) AS maxPrice,
                      COUNT(o.orderstatus) AS orderCount
              from Product p
              JOIN p.productDetails pd
              LEFT JOIN OrderDetail od ON pd.id = od.productDetail.id
              LEFT JOIN Order o ON od.order.id = o.id
                and o.orderstatus LIKE 'Hoàn thành'
                where (p.block = false and p.store.block = false) AND (p.store.taxcode is not null) and
               ((p.name like ?1 or p.name like ?2 or p.name like ?3 or p.name like ?4 or p.name like ?5 )
                   or (p.productcategory.name like ?1 or p.productcategory.name like ?2 or p.productcategory.name like ?3 or p.productcategory.name like ?4 or p.productcategory.name like ?5)
                   or (p.trademark.name like ?1 or  p.trademark.name like ?2 or p.trademark.name like ?3 or p.trademark.name like ?4 or p.trademark.name like ?5))

             GROUP BY p
            """)
    Page<Object[]> listProductPerMall(String name1, String name2, String name3, String name4, String name5,
            Pageable pageable);

    // Truy vấn lấy tất cả sản phẩm bị ban
    @Query("""
            select p from Product p where p.block = true
            """)
    List<Product> listAllProductBan();

    @Query(value = """
                                WITH ProductSales AS (
                SELECT
                    p.id AS productId,
                    pd.id AS productDetailId,
                    p.name AS productName,
                    pd.nameDetail AS nameDetail,
                    COALESCE(SUM(od.quantity), 0) AS sold,
                    MAX(pd.price) AS priceDetail,
                    (SELECT TOP 1 i.imageName
                     FROM Images i
                     WHERE i.productId = p.id) AS productImage,
                    pd.imageDetail AS imageNameDetail,
                    p.slug AS slugProduct
                FROM
                    Products p
                INNER JOIN
                    ProductDetails pd ON p.id = pd.idProduct
                LEFT JOIN
                    OrderDetails od ON pd.id = od.productDetailId
                LEFT JOIN
                    Orders o ON od.orderId = o.id
                WHERE
                    p.storeId = :storeId
                    AND o.orderStatus = 'Hoàn thành'
                    AND od.quantity IS NOT NULL
                GROUP BY
                    p.id, pd.id, p.name, pd.nameDetail, pd.imageDetail, p.slug
            )
            SELECT TOP 10
                productId,
                productDetailId,
                productName,
                nameDetail,
                priceDetail,
                sold,
                imageNameDetail,
                productImage,
                slugProduct
            FROM
                ProductSales
            ORDER BY
                sold DESC;

                                """, nativeQuery = true)
    List<Object[]> findTopSellingProductsByStoreId(@Param("storeId") Integer storeId);

    // Doanh thu theo năm seller
    @Query(value = "SELECT " +
            "    YEAR(o.paymentDate) AS year, " +
            "    SUM(od.quantity * od.price * (1 - pc.vat) * " +
            "        CASE " +
            "            WHEN o.idVoucher IS NOT NULL THEN " +
            "                1 - ISNULL(v.discountPrice, 0) / 100.0 " + // Applying discountPrice from Vouchers
            "            ELSE 1 " +
            "        END" +
            "    ) AS revenue, " +
            "    o.idVoucherAdmin, " + // Added idVoucherAdmin
            "    o.idVoucher AS voucherId, " + // Added voucherId
            "    v.discountPrice AS discountPrice " + // Added discountPrice
            "FROM " +
            "    Orders o " +
            "JOIN " +
            "    OrderDetails od ON o.id = od.orderId " +
            "JOIN " +
            "    ProductDetails pd ON od.productdetailid = pd.id " +
            "JOIN " +
            "    Products p ON p.id = pd.idProduct " +
            "JOIN " +
            "    ProductCategorys pc ON p.categoryId = pc.id " +
            "LEFT JOIN " +
            "    Vouchers v ON o.idVoucher = v.id " + // Left join with Vouchers table
            "WHERE " +
            "    o.storeId = :storeId " +
            "    AND o.orderStatus = N'hoàn thành' " + // Filter for completed orders
            "GROUP BY " +
            "    YEAR(o.paymentDate), o.idVoucherAdmin, o.idVoucher, v.discountPrice " + // Added new fields in GROUP BY
            "ORDER BY " +
            "    year", nativeQuery = true)
    List<Object[]> findRevenueByStoreId(@Param("storeId") Integer storeId);

    // all san pham
    @Query(value = """
                WITH ProductSales AS (
                    SELECT
                        pd.id AS productDetailId,
                        COALESCE(SUM(od.quantity), 0) AS sold
                    FROM
                        ProductDetails pd
                    LEFT JOIN
                        OrderDetails od ON pd.id = od.productDetailId
                    LEFT JOIN
                        Orders o ON od.orderId = o.id
                    WHERE
                        o.orderStatus = 'Hoàn thành'
                    GROUP BY
                        pd.id
                )
                SELECT
                    p.id AS productId,
                    p.name AS productName,  -- Added productName from Products table
                    pd.id AS productDetailId,
                    pd.nameDetail AS nameDetail,
                    pd.imageDetail AS imageDetail,
                    (SELECT TOP 1 i.imageName FROM Images i WHERE i.productId = p.id) AS imageName,
                    pd.price AS priceDetail,
                    pd.quantity AS quantityRemainingDetail,
                    COALESCE(ps.sold, 0) AS soldDetail,
                    pc.name AS nameCategory,
                    COALESCE(MAX(v.discountPrice), 0) AS discount
                FROM
                    Products p
                JOIN
                    ProductDetails pd ON p.id = pd.idProduct
                LEFT JOIN
                    ProductSales ps ON pd.id = ps.productDetailId
                JOIN
                    ProductCategorys pc ON p.categoryId = pc.id
                LEFT JOIN
                    VoucherAdminDetail vad ON pd.id = vad.idProduct
                LEFT JOIN
                    Vouchers v ON vad.idVoucherAdmin = v.id
                WHERE
                    p.storeId = :idStore
                GROUP BY
                    p.id, p.name, pd.id, pd.nameDetail, pd.imageDetail, pd.price, pd.quantity, pc.name, ps.sold
                ORDER BY
                    sold DESC, pd.nameDetail
            """, nativeQuery = true)
    List<Object[]> findAllProductDetailsByStore(@Param("idStore") int idStore);
}
