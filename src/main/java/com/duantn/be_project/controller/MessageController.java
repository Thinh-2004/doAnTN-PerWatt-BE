package com.duantn.be_project.controller;

import com.duantn.be_project.model.Message;
import com.duantn.be_project.Service.MessageService;
import com.duantn.be_project.Repository.StoreRepository;
import com.duantn.be_project.model.Store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private StoreRepository storeRepository;

    /// Lấy tin nhắn giữa hai người dùng cụ thể
    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop', 'Buyer_Manage_Buyer')")
    @GetMapping("/api/messages/{senderId}/{receiverId}")
    public ResponseEntity<?> getMessagesBetweenUsers(@PathVariable("senderId") Integer senderId,
            @PathVariable("receiverId") Integer receiverId) {
        List<Message> messagesBuyerSeller = messageService.getMessagesSeller(receiverId, senderId);
        if (messagesBuyerSeller.isEmpty() || messagesBuyerSeller == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(messagesBuyerSeller);
    }

    // // Lấy tin nhắn theo senderId và receiverId
    // @PreAuthorize("hasAnyAuthority('Seller', 'Buyer')")
    // @GetMapping("/api/messages/{senderId}/{receiverId}")
    // public List<Message> getMessagesByUsers(
    // @PathVariable String senderId,
    // @PathVariable String receiverId) {
    // return messageService.getMessagesBetweenUsers(senderId, receiverId);
    // }

    // Gửi tin nhắn qua WebSocket và lưu vào MongoDB
    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop', 'Buyer_Manage_Buyer')")
    @MessageMapping("/sendMessage")
    public ResponseEntity<Message> sendMessage(@RequestBody Message message) {
        messageService.saveMessage(message);
        messagingTemplate.convertAndSend("/topic/messages", message); // Phát tin nhắn qua WebSocket
        return ResponseEntity.ok(message);
    }

    // Thêm tin nhắn
    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop', 'Buyer_Manage_Buyer')")
    @PostMapping("/api/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        Message savedMessage = messageService.saveMessage(message);
        return ResponseEntity.ok(savedMessage);
    }

    // Sửa tin nhắn
    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop', 'Buyer_Manage_Buyer')")
    @PutMapping("/api/messages/{id}")
    public ResponseEntity<Message> updateMessage(
            @PathVariable String id,
            @RequestBody Message updatedMessage) {
        Message message = messageService.updateMessage(id, updatedMessage);
        return ResponseEntity.ok(message);
    }

    // Xóa tin nhắn
    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop', 'Buyer_Manage_Buyer')")
    @DeleteMapping("/api/messages/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable String id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }

}
