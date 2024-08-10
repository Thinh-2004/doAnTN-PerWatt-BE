package com.duantn.be_project.Repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.duantn.be_project.model.PaymentMethod;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {
   
}
