package com.duantn.be_project.controller;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Repository.CategoryRepository;
import com.duantn.be_project.model.ProductCategory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@CrossOrigin("*")
@RestController
public class CategoryController {
    @Autowired
    CategoryRepository categoryRepository;

    // GetAll
    @GetMapping("/category")
    public ResponseEntity<List<ProductCategory>> getAll(Model model) {
        List<ProductCategory> productCategories = categoryRepository.findAll();
        productCategories.sort(Comparator.comparing((ProductCategory pc) -> pc.getName()));
        return ResponseEntity.ok(productCategories);
    }
    @GetMapping("/category/hot")
    public ResponseEntity<List<ProductCategory>> getCategory(Model model) {
        List<ProductCategory> productCategories = categoryRepository.sortByPCAZ();
        return ResponseEntity.ok(productCategories); 
    }

    // GetAllById
    @GetMapping("/category/{id}")
    public ResponseEntity<ProductCategory> getById(@PathVariable("id") Integer id) {
        ProductCategory productCategory = categoryRepository.findById(id).orElseThrow();
        if (productCategory == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productCategory);
    }

    // Post
    @PostMapping("/category")
    public ResponseEntity<ProductCategory> post(@RequestBody ProductCategory productCategory) {
        // TODO: process POST request
        if (categoryRepository.existsById(productCategory.getId())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(categoryRepository.save(productCategory));
    }

  //Put
    @PutMapping("category/{id}")
    public ResponseEntity<ProductCategory> put(@PathVariable("id") Integer id,
            @RequestBody ProductCategory productCategory) {
        // TODO: process PUT request
        if (!categoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categoryRepository.save(productCategory));
    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        // TODO: process PUT request
        if (!categoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        categoryRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
