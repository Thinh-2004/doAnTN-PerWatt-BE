/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.duantn.be_project.Repository;

import com.duantn.be_project.model.Comment;
import com.duantn.be_project.model.Product;
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

    ArrayList<Comment> findByReply(Comment comment);

    // Bổ sung Thịnh
    @Query("""
            select c from Comment c where c.product.id = ?1 and c.reply is null
            """)
    List<Comment> commentByIdProduct(Integer idProduct);

}