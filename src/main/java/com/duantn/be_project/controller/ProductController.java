package com.duantn.be_project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Repository.ProductRepository;
import com.duantn.be_project.Repository.StoreRepository;
import com.duantn.be_project.model.Product;
import com.duantn.be_project.model.Store;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


@CrossOrigin("*")
@RestController
public class ProductController {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    StoreRepository storeRepository;

    // GetAll
    @GetMapping("/pageHome")
    public ResponseEntity<List<Product>> getAll(Model model) {
        return ResponseEntity.ok(productRepository.findAll());
    }

    // GetAllByIdStore
    @GetMapping("/productStore/{id}")
    public ResponseEntity<List<Product>> getAllProductByIdUser(@PathVariable("id") Integer storeId) {
        List<Product> products = productRepository.findAllByStoreId(storeId);
        if (products == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(products);
    }

    // GetByIdProduct
    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getByIdProduct(@PathVariable("id") Integer id) {
        Product product = productRepository.findById(id).orElseThrow();
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    // Post Store Product
    @PostMapping("/productCreate")
    public ResponseEntity<Product> post(@RequestBody Product product) {
        // TODO: process POST request
        if (productRepository.existsById(product.getId())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(productRepository.save(product));
    }

    // Put Store Product
    @PutMapping("ProductUpdate/{id}")
    public ResponseEntity<Product> put(@PathVariable("id") Integer id, @RequestBody Product product) {
        // TODO: process PUT request
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.save(product);
        return ResponseEntity.ok(product);
    }

    //Delete
    @DeleteMapping("/ProductDelete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        // TODO: process PUT request
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    //Tìm idStore
    @GetMapping("/searchStore/{id}")
    public ResponseEntity<Store> getIdStoreByIdUser(@PathVariable("id") Integer idUser) {
        Store store = storeRepository.findStoreByIdUser(idUser);
        if(store == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(store);
    }
    
}
