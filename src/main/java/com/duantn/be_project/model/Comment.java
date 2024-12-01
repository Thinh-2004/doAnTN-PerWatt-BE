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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Comments")
public class Comment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String content;

    @ManyToOne
    @JoinColumn(name = "productid")
    Product product;

    @ManyToOne
    @JoinColumn(name = "userid")
    User user;

    @ManyToOne
    @JoinColumn(name = "storeid")
    Store store;

    @Temporal(TemporalType.TIMESTAMP)
    Date commentdate;
    Integer rating;
    
    @ManyToOne
    @JoinColumn(name = "replyid")
    Comment reply;
}