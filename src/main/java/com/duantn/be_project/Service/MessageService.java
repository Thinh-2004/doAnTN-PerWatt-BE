package com.duantn.be_project.Service;


import com.duantn.be_project.Repository.MessageRepository;
import com.duantn.be_project.model.Message;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    //Lấy tất cả tin nhắn
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    // Lấy tin nhắn giữa senderId và receiverId

    public List<Message> getMessagesSeller(Integer receiverId, Integer senderId) {
        return messageRepository.findMessagesBySenderId(receiverId, senderId);
    }


    // Lưu tin nhắn
    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    // Sửa tin nhắn
    public Message updateMessage(String id, Message updatedMessage) {
        return messageRepository.findById(id)
                .map(existingMessage -> {
                    existingMessage.setContent(updatedMessage.getContent());
                    existingMessage.setTimestamp(updatedMessage.getTimestamp());
                    return messageRepository.save(existingMessage);
                })
                .orElseThrow(() -> new RuntimeException("Message not found"));
    }

    // Xóa tin nhắn
    public void deleteMessage(String id) {
        messageRepository.deleteById(id);
    }
    // public List<Message> getMessagesBetweenUsers(int senderId, int receiverId) {
    //     return messageRepository.findMessagesBySenderIdAndReceiverId(senderId, receiverId);
    // }
    
}
