package com.duantn.be_project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duantn.be_project.model.Banner;

public interface BannerRepository extends JpaRepository<Banner, Integer> {
    
}
