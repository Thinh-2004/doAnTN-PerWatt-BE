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

    @Temporal(TemporalType.TIMESTAMP)
    Date receivedate;

    @Temporal(TemporalType.TIMESTAMP)
    Date awaitingdeliverydate;

    @ManyToOne
    @JoinColumn(name = "paymentmethodid")
    PaymentMethod paymentmethod;

    @ManyToOne
    @JoinColumn(name = "userid")
    User user;

    @ManyToOne
    @JoinColumn(name = "shippingaddressid")
    ShippingInfor shippinginfor;

    @ManyToOne
    @JoinColumn(name = "Storeid")
    Store store;

    @ManyToOne
    @JoinColumn(name = "idvoucher")
    Voucher voucher;

    @ManyToOne
    @JoinColumn(name = "idvoucheradmin")
    VoucherAdmin voucherAdmin;

    @JsonIgnore
    @OneToMany(mappedBy = "order")
    List<OrderDetail> orderdetails;

    @JsonIgnore
    @OneToMany(mappedBy = "order")
    List<Report> reports;

    String note;

    Float totalamount;

}