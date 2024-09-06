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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
@Table(name = "Users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String fullname;
    String password;
    String email;
    @Temporal(TemporalType.DATE)
    Date birthdate;
    Boolean gender;

    @ManyToOne
    @JoinColumn(name = "roleid")
    Role role;

    String address;
    String phone;
    String avatar;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    List<Comment> comments;

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

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    List<Banner> banners;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    List<VoucherDetail> voucherDetails;

}
