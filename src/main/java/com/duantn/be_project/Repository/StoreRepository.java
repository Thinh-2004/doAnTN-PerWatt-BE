package com.duantn.be_project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.duantn.be_project.model.Store;
import com.duantn.be_project.model.User;

public interface StoreRepository extends JpaRepository<Store, Integer> {
    @Query("select s from Store s where s.user.id = ?1 ")
    public Store findStoreByIdUser(Integer idUser);

}
