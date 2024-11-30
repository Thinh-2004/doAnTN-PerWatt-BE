package com.duantn.be_project.controller;

import com.duantn.be_project.Repository.CommentRepository;
import com.duantn.be_project.Repository.ProductRepository;
import com.duantn.be_project.Repository.StoreRepository;
import com.duantn.be_project.Repository.UserRepository;
import com.duantn.be_project.model.Comment;
import com.duantn.be_project.model.Product;
import com.duantn.be_project.model.Store;
import com.duantn.be_project.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
public class CommentController {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductRepository productRepository;

    @GetMapping("/comment/list")
    public ResponseEntity<?> getComments(
            @RequestParam("productId") Integer productId,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit,
            @RequestParam("sort") String sort) {
        Product product = productRepository.findById(productId).orElseThrow();
        Pageable pageable = PageRequest.of(page - 1, limit,
                sort.equals("newest") ? Sort.by("commentdate").descending() : Sort.by("commentdate").ascending());
        Page<Comment> commentsPage = commentRepository.findByProductAndReplyIsNull(product, pageable);
        return ResponseEntity.ok(commentsPage.getContent());
    }

    @GetMapping("/comment/count")
    public int getCountComment(@RequestParam("productId") Integer productId) {
        Product product = productRepository.findById(productId).orElseThrow();
        return commentRepository.countByProduct(product);
    }

    @GetMapping("/comment/reply")
    public ResponseEntity<?> getCommentsReply(@RequestParam("commentId") Integer commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        ArrayList<Comment> comments = commentRepository.findByReply(comment);
        return ResponseEntity.ok(comments);
    }

    @PreAuthorize("hasAnyAuthority('Seller','Buyer')")
    @PostMapping("/comment")
    public ResponseEntity<?> addComment(@RequestBody Map<String, Object> data) {
        Comment comment = new Comment();
        comment.setId((Integer) data.get("id"));
        comment.setContent((String) data.get("content"));
        Product product = productRepository.findById((Integer) data.get("productId")).orElseThrow();
        comment.setProduct(product);
        User user = userRepository.findById((Integer) data.get("userId")).orElseThrow();
        comment.setUser(user);
        Store store = storeRepository.findById((Integer) data.get("storeId")).orElseThrow();
        comment.setStore(store);
        LocalDateTime commentDate = LocalDateTime.parse((String) data.get("commentDate"));
        comment.setCommentdate(Date.from(commentDate.atZone(ZoneId.systemDefault()).toInstant()));

        comment.setRating((Integer) data.get("rating"));

        if (data.containsKey("replyId") && data.get("replyId") != null) {
            Integer replyId = (Integer) data.get("replyId");
            Comment reply = commentRepository.findById(replyId).orElseThrow();
            comment.setReply(reply);
        }
        commentRepository.save(comment);
        return ResponseEntity.ok(comment);
    }

    @PreAuthorize("hasAnyAuthority('Seller','Buyer')")
    @PutMapping("/comment")
    public ResponseEntity<?> updateComment(@RequestBody Map<String, Object> data) {
        Comment comment = new Comment();
        comment.setId((Integer) data.get("id"));
        comment.setContent((String) data.get("content"));
        Product product = productRepository.findById((Integer) data.get("productId")).orElseThrow();
        comment.setProduct(product);
        User user = userRepository.findById((Integer) data.get("userId")).orElseThrow();
        comment.setUser(user);
        Store store = storeRepository.findById((Integer) data.get("storeId")).orElseThrow();
        comment.setStore(store);
        LocalDateTime commentDate = LocalDateTime.parse((String) data.get("commentDate"));
        comment.setCommentdate(Date.from(commentDate.atZone(ZoneId.systemDefault()).toInstant()));

        comment.setRating((Integer) data.get("rating"));

        if (data.containsKey("replyId") && data.get("replyId") != null) {
            Integer replyId = (Integer) data.get("replyId");
            Comment reply = commentRepository.findById(replyId).orElseThrow();
            comment.setReply(reply);
        }

        commentRepository.save(comment);
        return ResponseEntity.ok(comment);
    }

    @PreAuthorize("hasAnyAuthority('Seller','Buyer')")
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Integer commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        ArrayList<Comment> replies = commentRepository.findByReply(comment);
        for (Comment reply : replies) {
            commentRepository.delete(reply);
        }
        commentRepository.delete(comment);
        return ResponseEntity.ok(comment);
    }

    // Bổ sung của Thịnh
    @GetMapping("/comment/count/evaluate/{productId}")
    public ResponseEntity<List<Comment>> getCountCommentEvaluate(@PathVariable Integer productId) {
        List<Comment> Comment = commentRepository.commentByIdProduct(productId);
        return ResponseEntity.ok(Comment);
    }
}
