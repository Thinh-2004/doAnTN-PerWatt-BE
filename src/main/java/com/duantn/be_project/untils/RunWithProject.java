package com.duantn.be_project.untils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.duantn.be_project.Repository.BlockRepository;
import com.duantn.be_project.Repository.OrderRepository;
import com.duantn.be_project.Repository.ProductRepository;
import com.duantn.be_project.Repository.StoreRepository;
import com.duantn.be_project.Repository.VoucherDetailsSellerRepository;
import com.duantn.be_project.Repository.VoucherSellerRepository;
import com.duantn.be_project.model.Order;
import com.duantn.be_project.model.Product;
import com.duantn.be_project.model.Store;
import com.duantn.be_project.model.Voucher;

@Component // Đánh dấu lớp này là một Spring Bean
public class RunWithProject {
    @Autowired
    VoucherSellerRepository voucherSellerRepository;
    @Autowired
    VoucherDetailsSellerRepository voucherDetailsSellerRepository;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    BlockRepository blockRepository;
    @Autowired
    OrderRepository orderRepository;

    // @Async
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Ho_Chi_Minh") // Chạy mỗi ngày vào lúc 00:00
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

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Ho_Chi_Minh") // Chạy mỗi ngày vào lúc 00:00
    // @Scheduled(fixedRate = 3000) // Chạy mỗi 3 giây
    public void runDeleteVoucherDetail() {
        // runDateUpdate().join(); // Chờ method chạy xong thì mới được xóa
        voucherDetailsSellerRepository.deleteByVoucherStatus("Ngừng hoạt động");
    }

    // Kiểm tra cửa hàng
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Ho_Chi_Minh") // Chạy mỗi ngày vào lúc 00:00
    public CompletableFuture<Void> runDateUpdateStore() {
        // Lấy tất cả các store trong cơ sở dữ liệu
        List<Store> stores = storeRepository.listAllStoreByBan();

        // Lấy ngày hiện tại
        LocalDate currentDate = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")); // Lấy ngày hiện tại theo múi giờ của Việt
                                                                              // Nam

        // Kiểm tra và cập nhật trạng thái của từng store
        for (Store store : stores) {
            // Kiểm tra nếu endday không phải là null
            if (store.getEndday() != null) {
                // Chuyển đổi từ Date sang LocalDate với múi giờ đúng
                LocalDate endDay = store.getEndday().toInstant()
                        .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                        .toLocalDate();

                // Kiểm tra và cập nhật store nếu endDay bằng ngày hiện tại
                if (endDay.isEqual(currentDate)) {
                    store.setBlock(false);
                    store.setStatus("Không hiệu lực");
                    store.setStartday(null);
                    store.setEndday(null);
                    store.setReason(null);
                }

                // Cập nhật lại trạng thái store vào cơ sở dữ liệu
                storeRepository.save(store);
            }
        }
        return CompletableFuture.completedFuture(null); // Trả về khi hoàn thành
    }

    // Kiểm tra sản phẩm
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Ho_Chi_Minh") // Chạy mỗi ngày vào lúc 00:00
    public CompletableFuture<Void> runDateUpdateProduct() {
        // Lấy tất cả các store trong cơ sở dữ liệu
        List<Product> products = productRepository.listAllProductBan();

        // Lấy ngày hiện tại
        LocalDate currentDate = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")); // Lấy ngày hiện tại theo múi giờ của Việt
                                                                              // Nam

        // Kiểm tra và cập nhật trạng thái của từng store
        for (Product product : products) {
            if (product.getEndday() != null && !product.getStartday().equals(product.getEndday())) {
                // Chuyển đổi từ Date sang LocalDate với múi giờ đúng
                LocalDate endDay = product.getEndday().toInstant()
                        .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                        .toLocalDate();

                // Kiểm tra và cập nhật sản phẩm
                if (endDay.isEqual(currentDate)) {
                    product.setBlock(false);
                    product.setStatus("Không hiệu lực");
                    product.setStartday(null);
                    product.setEndday(null);
                    product.setReason(null);
                }

                // Cập nhật lại trạng thái sản phẩm vào cơ sở dữ liệu
                productRepository.save(product);
                // Xóa tất cả block theo sản phẩm được hủy ban
                blockRepository.deleteByIdProduct(product.getId());
            }
        }
        return CompletableFuture.completedFuture(null); // Trả về khi hoàn thành
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Ho_Chi_Minh") // Chạy mỗi ngày vào lúc 00:00
    public CompletableFuture<Void> runDateUpdateStusOrder() {
        // Lấy tất cả các đơn hàng đang chờ nhận
        List<Order> orders = orderRepository.listAllOrderAwaiting();

        // Lấy ngày hiện tại và thời gian hiện tại
        LocalDateTime currentDateTime = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")); // Lấy ngày giờ hiện tại theo
                                                                                          // múi giờ của Việt Nam

        // Kiểm tra và cập nhật trạng thái của từng đơn hàng
        for (Order order : orders) {
            // Kiểm tra nếu awaitingdeliverydate không phải là null
            if (order.getAwaitingdeliverydate() != null) {
                // Chuyển đổi từ Date sang LocalDateTime với múi giờ đúng
                LocalDateTime awaitingDeliveryDateTime = order.getAwaitingdeliverydate().toInstant()
                        .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                        .toLocalDateTime();

                // Kiểm tra nếu ngày hiện tại lớn hơn 1 phút so với awaitingdeliverydate
                if (currentDateTime.isAfter(awaitingDeliveryDateTime.plusDays(7))) {
                    // Cập nhật trạng thái order từ "Chờ nhận hàng" thành "Hoàn thành"
                    if ("Chờ nhận hàng".equals(order.getOrderstatus())) {
                        order.setOrderstatus("Hoàn thành");
                    }
                }

                // Cập nhật lại trạng thái order vào cơ sở dữ liệu
                orderRepository.save(order);
            }
        }
        return CompletableFuture.completedFuture(null); // Trả về khi hoàn thành
    }

}
