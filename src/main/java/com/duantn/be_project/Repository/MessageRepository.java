package com.duantn.be_project.Repository;

import com.duantn.be_project.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    // Tìm tất cả tin nhắn giữa người gửi và người nhận
    List<Message> findBySenderIdAndReceiverId(String senderId, String receiverId);

    // Tìm tất cả tin nhắn của một người gửi
    List<Message> findBySenderId(String senderId);

    // Tìm tất cả tin nhắn của một người nhận
    List<Message> findByReceiverId(String receiverId);

    // Tìm tin nhắn theo ID
    Optional<Message> findById(String id);

    @Query("{ '$or': [ { 'senderId': ?0, 'receiverId': ?1 }, { 'senderId': ?1, 'receiverId': ?0 } ] }")
    List<Message> findMessagesBySenderId(Integer senderId, Integer receiverId);

}
