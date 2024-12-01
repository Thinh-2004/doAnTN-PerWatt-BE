package com.duantn.be_project.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.duantn.be_project.model.Banner;

public interface BannerRepository extends JpaRepository<Banner, Integer> {
    //Truy vấn để tìm banner middle
    @Query("""
            select b from Banner b where b.position in ('MID', 'MIDTOP', 'MIDBOT')
            """)
            List<Banner> findBannerByParameter();
}
