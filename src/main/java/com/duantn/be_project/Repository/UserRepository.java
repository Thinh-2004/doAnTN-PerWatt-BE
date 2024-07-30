package com.duantn.be_project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.duantn.be_project.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("select u from User u where u.email like ?1 ")
    public User findByEmail(String email);
}
