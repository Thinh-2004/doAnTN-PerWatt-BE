package com.duantn.be_project.model;

import java.time.LocalDateTime;

import org.bson.BsonTimestamp;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
@Document(collection = "message")
public class Message {
    @Id
    private String id;
    private Integer senderId;
    private Integer receiverId;
    private String content;
    private LocalDateTime timestamp;
}
