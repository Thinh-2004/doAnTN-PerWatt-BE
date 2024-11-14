package com.duantn.be_project.untils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.duantn.be_project.Repository.VoucherDetailsSellerRepository;
import com.duantn.be_project.Repository.VoucherSellerRepository;
import com.duantn.be_project.model.Voucher;

@Component // Đánh dấu lớp này là một Spring Bean
public class RunWithProject {
    @Autowired
    VoucherSellerRepository voucherSellerRepository;
    @Autowired
    VoucherDetailsSellerRepository voucherDetailsSellerRepository;

    @Async
    @Scheduled(cron = "0 0 0 * * ?") // Chạy mỗi ngày vào lúc 00:00
    // @Scheduled(fixedRate = 3000) // Chạy mỗi 3 giây
    public CompletableFuture<Void> runDateUpdate() {
        // Lấy tất cả các voucher trong cơ sở dữ liệu
        List<Voucher> vouchers = voucherSellerRepository.findAll();

        // Lấy ngày hiện tại
        LocalDate currentDate = LocalDate.now();

        // Kiểm tra và cập nhật trạng thái của từng voucher
        for (Voucher voucher : vouchers) {
            // Chuyển đổi startday và endday từ Date sang LocalDate
            LocalDate startDay = voucher.getStartday().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            LocalDate endDay = voucher.getEndday().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            System.out.println("Voucher trước khi cập nhật: " + voucher.getStatus());
            // Kiểm tra và cập nhật trạng thái voucher
            if (startDay.isEqual(currentDate)) {
                voucher.setStatus("Hoạt động");
            } else if (endDay.isEqual(currentDate)) {
                voucher.setStatus("Ngừng hoạt động");
            } else if (currentDate.isAfter(endDay)) {
                voucher.setStatus("Ngừng hoạt động");
            }
            System.out.println("Voucher sau khi cập nhật: " + voucher.getStatus());

            // Cập nhật lại trạng thái voucher vào cơ sở dữ liệu
            voucherSellerRepository.save(voucher);
        }
        return CompletableFuture.completedFuture(null); // Trả về khi hoàn thành
    }

    @Scheduled(cron = "0 0 0 * * ?") // Chạy mỗi ngày vào lúc 00:00
    // @Scheduled(fixedRate = 3000) // Chạy mỗi 3 giây
    public void runDeleteVoucherDetail() {
        runDateUpdate().join(); // Chờ method chạy xong thì mới được xóa
        voucherDetailsSellerRepository.deleteByVoucherStatus("Ngừng hoạt động");
    }
}
