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
@Table(name = "Orders")
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String orderstatus;
    @Temporal(TemporalType.TIMESTAMP)
    Date paymentdate;

    @ManyToOne
    @JoinColumn(name = "paymentMethodid")
    PaymentMethod paymentmethod;

    @ManyToOne
    @JoinColumn(name = "userid")
    User user;

    @ManyToOne
    @JoinColumn(name = "shippingaddressid")
    ShippingInfor shippinginfor;

    @ManyToOne
    @JoinColumn(name = "feeid")
    Fee fee;

    @JsonIgnore
    @OneToMany(mappedBy = "order")
    List<Voucher> vouchers;

    @JsonIgnore
    @OneToMany(mappedBy = "order")
    List<OrderDetail> orderdetails;

}