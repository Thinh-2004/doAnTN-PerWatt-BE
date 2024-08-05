package com.duantn.be_project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duantn.be_project.model.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
  

}
