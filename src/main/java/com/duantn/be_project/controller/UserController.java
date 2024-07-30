package com.duantn.be_project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Repository.UserRepository;
import com.duantn.be_project.model.User;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@CrossOrigin("*")
@RestController
public class UserController {
    @Autowired
    UserRepository userRepository;
    // GetAll
    @GetMapping("/user")
    public ResponseEntity<List<User>> getAll(Model model) {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // GetByEmail
    @GetMapping("/user/{email}")
    public ResponseEntity<User> getByEmail(Model model, @PathVariable("email") String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    // Post
    @PostMapping("/user")
    public ResponseEntity<User> post(@RequestBody User user) {
        // TODO: process POST request
        if(userRepository.existsById(user.getId())){
            return ResponseEntity.badRequest().build();
        }
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    // Put
    @PutMapping("/user/{id}")
    public ResponseEntity<User> put(@PathVariable("id") Integer id, @RequestBody User user) {
        // TODO: process PUT request
        if(!userRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    // delete
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        if(!userRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
