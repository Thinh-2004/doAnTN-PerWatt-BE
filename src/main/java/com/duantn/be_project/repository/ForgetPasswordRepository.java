package com.duantn.be_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duantn.be_project.model.User;

public interface ForgetPasswordRepository extends JpaRepository<User, Integer> {
	public User findByEmail(String email);
}
