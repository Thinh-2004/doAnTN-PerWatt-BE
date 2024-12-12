package com.duantn.be_project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Repository.BlockRepository;
import com.duantn.be_project.model.Block;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@CrossOrigin("*")
public class BlockController {
    @Autowired
    BlockRepository blockRepository;

    @PreAuthorize("hasAnyAuthority('Admin_All_Function', 'Admin_Manage_Support')")
    @GetMapping("/list/block/product/{idProduct}")
    public ResponseEntity<List<Block>> getMethodName(@PathVariable("idProduct") Integer idProduct) {
        List<Block> blocks = blockRepository.listBlockByIdProduct(idProduct);
        if (blocks == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(blocks);
    }

}
