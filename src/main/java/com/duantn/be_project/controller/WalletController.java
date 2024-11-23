package com.duantn.be_project.controller;

import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Repository.UserRepository;
import com.duantn.be_project.Repository.WalletRepository;
import com.duantn.be_project.Repository.WalletTransactionRepository;
import com.duantn.be_project.model.User;
import com.duantn.be_project.model.Wallet;
import com.duantn.be_project.model.WalletTransaction;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin("*")
@RestController
public class WalletController {

    @Autowired
    WalletRepository walletRepository;
    @Autowired
    WalletTransactionRepository walletTransactionRepository;
    @Autowired
    UserRepository userRepository;

    @PreAuthorize("hasAnyAuthority('Seller', 'Buyer', 'Admin')")
    @GetMapping("/wallet/{id}")
    public ResponseEntity<Wallet> getByUserId(@PathVariable("id") Integer id) {
        Optional<Wallet> optionalWallet = walletRepository.findByUserId(id);

        return ResponseEntity.ok(optionalWallet.get());
    }

    @PreAuthorize("hasAnyAuthority('Seller', 'Buyer', 'Admin')")
    @PutMapping("/wallet/update/{id}")
    public ResponseEntity<Wallet> updateBalance(@PathVariable("id") Integer id, @RequestBody Wallet updatedWallet) {
        Optional<Wallet> optionalWallet = walletRepository.findByUserId(id);

        Wallet wallet = optionalWallet.get();
        wallet.setBalance(updatedWallet.getBalance());
        walletRepository.save(wallet);
        return ResponseEntity.ok(wallet);
    }

    @PreAuthorize("hasAnyAuthority('Seller', 'Buyer', 'Admin')")
    @GetMapping("/wallettransaction/{id}")
    public ResponseEntity<List<WalletTransaction>> getByWalletId(@PathVariable("id") Integer id) {
        List<WalletTransaction> walletTransactions = walletTransactionRepository.findByWalletId(id);
        return ResponseEntity.ok(walletTransactions);
    }

    @PreAuthorize("hasAnyAuthority('Seller', 'Buyer', 'Admin')")
    @PostMapping("/wallettransaction/create/{id}")
    public ResponseEntity<String> addTransaction(@PathVariable("id") Integer id,
            @RequestBody WalletTransaction newTransaction) {
        Wallet wallet = walletRepository.findById(id).orElse(null);
        if (wallet == null) {
            throw new RuntimeException("Ví không tồn tại");
        }
        Integer userId = newTransaction.getUser().getId();
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("Người dùng không tồn tại");
        }
        newTransaction.setWallet(wallet);
        newTransaction.setUser(user);
        walletTransactionRepository.save(newTransaction);
        return ResponseEntity.status(201).body("Giao dịch đã được thêm thành công");
    }

    @PreAuthorize("hasAnyAuthority('Seller', 'Buyer', 'Admin')")
    @GetMapping("/wallettransaction/idWalletByIdUSer/{id}")
    public ResponseEntity<?> idWalletByIdUser(@PathVariable("id") Integer id) {
        Wallet wallet = walletRepository.findByUserId(id).orElse(null);
        return ResponseEntity.ok(wallet);
    }

}