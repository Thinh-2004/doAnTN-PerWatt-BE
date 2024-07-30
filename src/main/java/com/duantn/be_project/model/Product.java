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
    @JoinColumn(name = "categoryid")
    ProductCategory productcategory;

    @ManyToOne
    @JoinColumn(name = "trademarkid")
    TradeMark trademark;

    @ManyToOne
    @JoinColumn(name = "warrantiesid")
    Warranties warranties;

    BigDecimal price;
    Integer quantity;
    String size;
    String specializedgame;
    String description;

    @ManyToOne
    @JoinColumn(name = "storeid")
    Store store;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    List<ChatMessage> chatmessages;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    List<CartItem> cartitems;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    List<OrderDetail> orderdetails;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    List<Image> images;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    List<Comment> comments;
}
