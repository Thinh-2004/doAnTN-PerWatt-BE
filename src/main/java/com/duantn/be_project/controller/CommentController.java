package com.duantn.be_project.controller;

import com.duantn.be_project.Repository.CommentRepository;
import com.duantn.be_project.Repository.OrderRepository;
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
    @Autowired
    OrderRepository orderRepository;

    @GetMapping("/comment/list")
    public ResponseEntity<?> getComments(
            @RequestParam("productId") Integer productId,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "ratingSort", required = false) String ratingSort) {

        Product product = productRepository.findById(productId).orElseThrow();

        // Xác định sắp xếp theo ngày
        Sort dateSort = sort.equals("newest") ? Sort.by("commentdate").descending()
                : Sort.by("commentdate").ascending();

        // Xác định sắp xếp theo số sao (rating)
        Sort ratingSortObj = null;
        if (ratingSort != null) {
            if (ratingSort.equals("highest")) {
                ratingSortObj = Sort.by("rating").descending(); // Sắp xếp theo số sao từ cao đến thấp
            } else if (ratingSort.equals("lowest")) {
                ratingSortObj = Sort.by("rating").ascending(); // Sắp xếp theo số sao từ thấp đến cao
            }
        }

        // Chọn ra cách sắp xếp cuối
        Sort finalSort = (ratingSortObj != null) ? ratingSortObj : dateSort;

        Pageable pageable = PageRequest.of(page - 1, limit, finalSort);
        Page<Comment> commentsPage = commentRepository.findByProductAndReplyIsNull(product, pageable);

        return ResponseEntity.ok(commentsPage.getContent());
    }

    @GetMapping("/comment/count")
    public int getCountComment(@RequestParam("productId") Integer productId) {
        Product product = productRepository.findById(productId).orElseThrow();
        return commentRepository.countByProductAndReplyIsNull(product);
    }

    @GetMapping("/comment/evaluate/store/{idStore}")
    public ResponseEntity<Float> AvageEvaluate(@PathVariable("idStore") Integer idStore) {
        List<Integer> averageEvaluates = commentRepository.evaluateByStore(idStore);
        if (averageEvaluates.isEmpty()) {
            return ResponseEntity.ok(0.0f);
        }
        // Tính trung bình đánh giá dựa vào tất cả comment sản phẩm
        int totalStars = averageEvaluates.stream().mapToInt(Integer::intValue).sum();
        return ResponseEntity.ok((float) totalStars / averageEvaluates.size());
    }

    @GetMapping("/comment/reply")
    public ResponseEntity<?> getCommentsReply(@RequestParam("commentId") Integer commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        ArrayList<Comment> comments = commentRepository.findByReply(comment);
        return ResponseEntity.ok(comments);
    }

    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop', 'Buyer_Manage_Buyer')")
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

    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop', 'Buyer_Manage_Buyer')")
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

    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop', 'Buyer_Manage_Buyer')")
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

    // Bổ sung mới

    @GetMapping("/comment/count/user")
    public int getCountCommentByProductOfUser(
            @RequestParam("userId") Integer userId,
            @RequestParam("productId") Integer productId) {
        Product product = productRepository.findById(productId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        return commentRepository.countByProductAndUser(product, user);
    }

    @GetMapping("/comment/count/ordered")
    public int getCountOrderedByMainProductOfUser(
            @RequestParam("userId") Integer userId,
            @RequestParam("productId") Integer productId) {
        return orderRepository.countOrderBuyedOfProductByUser(userId, productId);
    }

    // Bổ sung của Thịnh
    @GetMapping("/comment/count/evaluate/{productId}")
    public ResponseEntity<List<Comment>> getCountCommentEvaluate(@PathVariable Integer productId) {
        List<Comment> Comment = commentRepository.commentByIdProduct(productId);
        return ResponseEntity.ok(Comment);
    }
}
