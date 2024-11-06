package com.duantn.be_project.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.duantn.be_project.model.WalletTransaction;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Integer> {
    @Query("select w from WalletTransaction w where w.wallet.id = ?1 order by w.transactiondate desc")
    List<WalletTransaction> findByWalletId(Integer idWallet);

}