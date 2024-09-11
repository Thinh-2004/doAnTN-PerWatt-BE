package com.duantn.be_project.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

    String size;
    String specializedgame;
    String description;

    @ManyToOne
    @JoinColumn(name = "storeid")
    Store store;

    // @JsonIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    List<Image> images;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    List<Comment> comments;

    @JsonManagedReference
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<ProductDetail> productDetails;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    List<VoucherAdminDetail> voucherAdminDetails;
}
