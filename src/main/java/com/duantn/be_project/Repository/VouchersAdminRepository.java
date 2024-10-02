package com.duantn.be_project.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.duantn.be_project.model.VoucherAdmin;
public interface VouchersAdminRepository extends JpaRepository<VoucherAdmin, Integer> {
    // Có thể thêm các phương thức tùy chỉnh ở đây nếu cần
}
