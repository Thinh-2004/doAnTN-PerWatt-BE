package com.duantn.be_project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Repository.WarrantiesRepository;
import com.duantn.be_project.model.Warranties;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@CrossOrigin("*")
@RestController
public class WarrantiesController {
    @Autowired
    WarrantiesRepository warrantiesRepository;

    // GetAll
    @GetMapping("/warranties")
    public ResponseEntity<List<Warranties>> get(Model model) {
        return ResponseEntity.ok(warrantiesRepository.findAll());
    }

    // GetById
    @GetMapping("/warranties/{id}")
    public ResponseEntity<Warranties> getById(@PathVariable("id") Integer id) {
        Warranties warranties = warrantiesRepository.findById(id).orElseThrow();
        if (warranties == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    // Post
    @PreAuthorize("hasAnyAuthority('Admin')")
    @PostMapping("/warranties")
    public ResponseEntity<Warranties> post(@RequestBody Warranties warranties) {
        // TODO: process POST request
        if (warrantiesRepository.existsById(warranties.getId())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(warrantiesRepository.save(warranties));
    }

    // Put
    @PreAuthorize("hasAnyAuthority('Admin')")
    @PutMapping("/warranties/{id}")
    public ResponseEntity<Warranties> put(@PathVariable("id") Integer id, @RequestBody Warranties warranties) {
        // TODO: process PUT request
        if (!warrantiesRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(warrantiesRepository.save(warranties));
    }

    @PreAuthorize("hasAnyAuthority('Admin')")
    @DeleteMapping("/warranties/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        // TODO: process PUT request
        if (!warrantiesRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        warrantiesRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
