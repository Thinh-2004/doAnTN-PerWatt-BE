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
    String vouchername;

    @ManyToOne
    @JoinColumn(name = "idorder")
    Order order;

    @ManyToOne
    @JoinColumn(name = "idstore")
    Store store;

    @Temporal(TemporalType.TIMESTAMP)
    Date startday;
    @Temporal(TemporalType.TIMESTAMP)
    Date endday;
}
