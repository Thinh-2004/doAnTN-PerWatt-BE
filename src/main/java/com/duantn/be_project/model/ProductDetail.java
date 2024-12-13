package com.duantn.be_project.model;

import java.io.Serializable;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "productdetails")
public class ProductDetail implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String namedetail;
    Float price;
    Integer quantity;

    // @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "idproduct")
    Product product;

    @JsonIgnore
    @OneToMany(mappedBy = "productDetail")
    List<OrderDetail> orderDetails;

    @JsonIgnore
    @OneToMany(mappedBy = "productDetail")
    List<CartItem> cartItems;


    

    String imagedetail;

    public void add(ProductDetail newProductDetail) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'add'");
    }

    public boolean contains(ProductDetail newProductDetail) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'contains'");
    }
}
