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
import lombok.Data;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "Stores")
public class Store implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String nameStore;
    String address;
    String email;
    String phone;
    Integer cccdNumber;
    @Temporal(TemporalType.TIMESTAMP)
    Date createdTime;
    String imgBackgound;

    @ManyToOne
    @JoinColumn(name = "userId")
    User user;

    @JsonIgnore
    @OneToMany(mappedBy = "store")
    List<Product> products;

    @JsonIgnore
    @OneToMany(mappedBy = "store")
    List<Voucher> vouchers;

    @JsonIgnore
    @OneToMany(mappedBy = "store")
    List<Follow> follows;
}
