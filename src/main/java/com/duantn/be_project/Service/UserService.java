package com.duantn.be_project.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.duantn.be_project.Repository.UserRepository;
import com.duantn.be_project.model.User;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);
        return (user != null && user.getPassword().equals(password)) ? user : null;
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUser(Integer id, User user) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        existingUser.setFullname(user.getFullname());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(passwordEncoder.encode(user.getPassword())); // Xử lý mã hóa mật khẩu nếu cần
        existingUser.setAddress(user.getAddress());
        existingUser.setPhone(user.getPhone());
        return userRepository.save(existingUser);
    }
}
