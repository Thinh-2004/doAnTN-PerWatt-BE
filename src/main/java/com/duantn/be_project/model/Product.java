package com.duantn.be_project.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "Products")
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String name;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    ProductCategory productCategory;

    @ManyToOne
    @JoinColumn(name = "trademarkId")
    TradeMark trademark;

    @ManyToOne
    @JoinColumn(name = "warrantiesId")
    Warranties warranties;

    BigDecimal price;
    Integer quantity;
    String size;
    String specializedGame;
    String description;

    @ManyToOne
    @JoinColumn(name = "storeId")
    Store store;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    List<ChatMessage> chatMessages;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    List<CartItem> cartItems;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    List<OrderDetail> orderDetails;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    List<Image> images;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    List<Comment> comments;
}
