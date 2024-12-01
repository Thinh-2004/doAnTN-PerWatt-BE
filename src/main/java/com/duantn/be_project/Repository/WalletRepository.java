package com.duantn.be_project.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.duantn.be_project.model.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {

    @Query("select w from Wallet w where w.user.id = ?1")
    Optional<Wallet> findByUserId(Integer idUser);

    @Query("select w from Wallet w where w.user.id = ?1")
    Wallet findByUserIdStoreId(Integer idUser);
}