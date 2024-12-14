package com.duantn.be_project.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.duantn.be_project.model.ShippingInfor;

public interface ShippingInfosRepository extends JpaRepository<ShippingInfor, Integer> {
    @Query("select s from ShippingInfor s where s.user.id = ?1")
    List<ShippingInfor> findAllByUserId(Integer idUser);

    @Modifying
    @Query("UPDATE ShippingInfor s SET s.isdefault = false WHERE s.user.id = :userId")
    void updateIsDefaultFalseByUserId(@Param("userId") Integer userId);

    List<ShippingInfor> findByUserId(Integer userId);

}
