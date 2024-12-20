package com.duantn.be_project.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
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
@Table(name = "Vouchersadmin")
public class VoucherAdmin implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String vouchername;

    @ManyToOne
    @JoinColumn(name = "idcatevoucher")
    VoucherAdminCategory voucherAdminCategory;

    String status;

    @Temporal(TemporalType.TIMESTAMP)
    Date startday;
    @Temporal(TemporalType.TIMESTAMP)
    Date endday;

    @JsonIgnore
    @OneToMany(mappedBy = "voucherAdmin")
    List<Order> orders;
    
    @JsonIgnore
    @OneToMany(mappedBy = "voucherAdmin" , cascade = CascadeType.ALL, orphanRemoval = true)
    List<VoucherAdminDetail> voucherAdminDetails;
}
