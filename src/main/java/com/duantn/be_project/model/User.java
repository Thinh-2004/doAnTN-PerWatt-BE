package com.duantn.be_project.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
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
    @JoinColumn(name = "rolepermissionid")
    RolePermission rolepPermission;

    String address;
    String phone;
    String avatar;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    List<Comment> comments;
<<<<<<< HEAD

    @JsonIgnore
    @OneToMany(mappedBy = "user1")
    List<ChatMessage> chatMessages1;

    @JsonIgnore
    @OneToMany(mappedBy = "user2")
    List<ChatMessage> chatMessages2;
=======
>>>>>>> 2fb2db93120c2a41361d8cf63fde495227a53b0c

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

<<<<<<< HEAD
=======
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    List<Banner> banners;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    List<VoucherDetail> voucherDetails;

>>>>>>> 2fb2db93120c2a41361d8cf63fde495227a53b0c
}
