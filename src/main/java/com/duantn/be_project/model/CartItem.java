 package com.duantn.be_project.model;

import java.io.Serializable;

import org.hibernate.annotations.ManyToAny;

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
@Table(name = "CartItems")
public class CartItem implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    Integer quantity;

    @ManyToOne
    @JoinColumn(name = "userId")
    User user;

    @ManyToOne
    @JoinColumn(name = "productId")
    Product product;
}
