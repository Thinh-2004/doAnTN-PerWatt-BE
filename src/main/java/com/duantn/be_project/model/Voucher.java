package com.duantn.be_project.model;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "Vouchers")
public class Voucher implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String voucherName;

    @ManyToOne
    @JoinColumn(name = "idOrder")
    Order order;

    @ManyToOne
    @JoinColumn(name = "idStore")
    Store store;

    @Temporal(TemporalType.TIMESTAMP)
    Date startDay;
    @Temporal(TemporalType.TIMESTAMP)
    Date endDay;
}
