package com.duantn.be_project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duantn.be_project.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}
