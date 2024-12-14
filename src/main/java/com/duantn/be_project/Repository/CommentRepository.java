/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.duantn.be_project.Repository;

import com.duantn.be_project.model.Comment;
import com.duantn.be_project.model.Product;
import com.duantn.be_project.model.User;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author DELL
 */
public interface CommentRepository extends JpaRepository<Comment, Integer> {
        Page<Comment> findByProductAndReplyIsNull(Product product, Pageable pageable);

        int countByProduct(Product product);

        int countByProductAndUser(Product product, User user);

        int countByProductAndReplyIsNull(Product product);

        ArrayList<Comment> findByReply(Comment comment);

        // Bổ sung Thịnh
        @Query("""
                        select c from Comment c where c.product.id = ?1 and c.reply is null
                        """)
        List<Comment> commentByIdProduct(Integer idProduct);

        // tính đánh giá trung bình cửa hàng
        @Query("""
                        select c.rating from Comment c where c.reply is null and c.store.id = ?1
                        """)
        List<Integer> evaluateByStore(Integer idStore);

}
