package com.duantn.be_project.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.duantn.be_project.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
  @Query("select p from Product p where p.store.id = ?1 ")
  List<Product> findAllByStoreId(Integer idStore);

  // @Query("SELECT p, MAX(pd.price) as maxPrice FROM Product p JOIN
  // p.productDetails pd WHERE p.store.slug LIKE ?1 AND (p.name LIKE ?2 OR
  // p.productcategory.name LIKE ?2 OR p.productcategory.id = ?3 OR
  // p.trademark.name LIKE ?2) GROUP BY p")
  // Page<Object[]> findAllByStoreIdWithSlugStore(String slugStore, String name,
  // Integer idCate, Pageable pageable);

  @Query("SELECT p, MAX(pd.price) as maxPrice, COUNT(od.id) as orderCount " +
      "FROM Product p " +
      "JOIN p.productDetails pd " +
      "LEFT JOIN OrderDetail od ON pd.id = od.productDetail.id " +
      "LEFT JOIN Order o ON od.order.id = o.id AND o.orderstatus LIKE 'Hoàn Thành' " +
      "WHERE p.store.slug LIKE ?1 " +
      "AND (p.name LIKE ?2 OR p.productcategory.name LIKE ?2 OR p.productcategory.id = ?3 OR p.trademark.name LIKE ?2) "
      +
      "GROUP BY p")
  Page<Object[]> findAllByStoreIdWithSlugStore(String slugStore, String name, Integer idCate, Pageable pageable);

  @Query("select p from Product p order by p.id desc")
  List<Product> findAllDesc();

  @Query("select p from Product p where p.store.id = ?1")
  List<Product> CountProductByIdStore(Integer id);

  Optional<Product> findBySlug(String slug); // Thêm phương thức tìm theo slug

  // Phương thức kiểm tra tồn tại bằng slug
  boolean existsBySlug(String slug);

  // Find by namePr, nameCate, nameTrademark
  @Query("select p from Product p where p.name like ?1 or p.productcategory.name like ?1 or p.productcategory.id = ?2 or p.trademark.name like ?1")
  Page<Product> findByNamePrCateTrademark(String name, Integer idCate, Pageable pageable);

  @Query("""
          SELECT p,
                 MAX(pd.price) AS maxPrice,
                 COUNT(od.id) AS orderCount
          FROM Product p
          JOIN p.productDetails pd
          LEFT JOIN OrderDetail od ON pd.id = od.productDetail.id
          LEFT JOIN Order o ON od.order.id = o.id
          AND o.orderstatus LIKE 'Hoàn thành'
          WHERE (p.productcategory.name = ?1 OR p.trademark.name = ?1)
            AND (p.name LIKE ?2)
            AND pd.price BETWEEN ?3 AND ?4
            AND ((?5 is null or 1 IN (?5) AND p.store.taxcode IS NOT NULL)
            OR (?5 is null or 2 IN (?5) AND p.store.taxcode IS NULL))
            AND (?6 IS NULL OR (p.trademark.name IN (?6)))
          GROUP BY p
      """)
  Page<Object[]> queryFindMore(String nameUrl, String name, Integer minPrice, Integer maxPrice,
      List<Integer> shopType, List<String> tradeMark, Pageable pageable);

  @Query("""
      SELECT p,
                MAX(pd.price) AS maxPrice,
                COUNT(od.id) AS orderCount
         FROM Product p
         JOIN p.productDetails pd
         LEFT JOIN OrderDetail od ON pd.id = od.productDetail.id
         LEFT JOIN Order o ON od.order.id = o.id
         AND o.orderstatus LIKE 'Hoàn thành'
         WHERE (p.productcategory.name = ?1 OR p.trademark.name = ?1)
           AND (p.name LIKE ?2)
           AND pd.price BETWEEN ?3 AND ?4
           AND ((?5 is null or 1 IN (?5) AND p.store.taxcode IS NOT NULL)
           OR (?5 is null or 2 IN (?5) AND p.store.taxcode IS NULL))
           AND (?6 IS NULL OR (p.trademark.name IN (?6)))
         GROUP BY p
                  """)
  List<Object[]> queryFindMoreFullList(String nameUrl, String name, Integer minPrice, Integer maxPrice,
      List<Integer> shopType, List<String> tradeMark, Sort sort);

  // Danh sách sản phẩm có taxcpde
  @Query("""
      select p from Product p where p.store.taxcode is not null
      """)
  Page<Product> listProductPerMall(Pageable pageable);
}
