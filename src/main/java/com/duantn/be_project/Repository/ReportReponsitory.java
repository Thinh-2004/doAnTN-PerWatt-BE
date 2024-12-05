package com.duantn.be_project.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.duantn.be_project.model.Report;

public interface ReportReponsitory extends JpaRepository<Report, Integer> {

    // List report wait for process
    @Query("""
            select r from Report r where r.user.id = ?1 and r.status like ?2
            """)
    Page<Report> listReportByStatus(Integer idUser, String status, Pageable pageable);

    // List All report
    @Query("""
            select r from Report r where r.status like ?1
            """)

    Page<Report> listAllReportByStatus(String status, Pageable pageable);
}
