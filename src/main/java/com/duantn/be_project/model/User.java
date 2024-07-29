package com.duantn.be_project.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "Users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String fullName;
    String password;
    String email;
    Date bithDate;
    Boolean gender;

    @ManyToOne
    @JoinColumn(name = "roleId")
    Role role;

    String address;
    String phone;
    String avatar;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    List<Comment> comments;    

    @JsonIgnore
    @OneToMany(mappedBy = "user1")
    List<ChatMessage> chatMessages1;  

    @JsonIgnore
    @OneToMany(mappedBy = "user2")
    List<ChatMessage> chatMessages2; 
    
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    List<Store> stores;  

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    List<Follow> follows;  

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    List<CartItem> cartItems;
    
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    List<Order> orders; 
    
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    List<ShippingInfor> shippingInfors; 
    
}
