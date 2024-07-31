package com.duantn.be_project.model;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "ChatMessages")
public class ChatMessage implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "senderid")
    User user1;

    @ManyToOne
    @JoinColumn(name = "receiverid")
    User user2;

    @ManyToOne
    @JoinColumn(name = "productid")
    Product product;
    

}
