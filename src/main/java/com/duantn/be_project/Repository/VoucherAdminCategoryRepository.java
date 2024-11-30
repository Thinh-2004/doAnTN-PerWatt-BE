package com.duantn.be_project.Repository;

import com.duantn.be_project.model.VoucherAdminCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherAdminCategoryRepository extends JpaRepository<VoucherAdminCategory, Integer> {
}
