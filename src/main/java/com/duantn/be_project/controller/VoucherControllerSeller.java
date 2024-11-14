package com.duantn.be_project.controller;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Repository.ProductDetailRepository;
import com.duantn.be_project.Repository.ProductRepository;
import com.duantn.be_project.Repository.VoucherSellerRepository;
import com.duantn.be_project.Service.SlugText.SlugText;
import com.duantn.be_project.model.Product;
import com.duantn.be_project.model.ProductDetail;
import com.duantn.be_project.model.Voucher;
import com.duantn.be_project.model.Request_Response.VoucherRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@CrossOrigin("*")
public class VoucherControllerSeller {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductDetailRepository productDetailRepository;
    @Autowired
    VoucherSellerRepository voucherSellerRepository;
    @Autowired
    SlugText slugText;

    @GetMapping("fillProduct/{idStore}")
    public ResponseEntity<List<Product>> getProduct(@PathVariable Integer idStore) {
        List<Product> products = productRepository.findAllByStoreId(idStore);
        if (products == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("fillProductDetails/{idProduct}")
    public ResponseEntity<List<ProductDetail>> getProductDetails(@PathVariable Integer idProduct) {
        List<ProductDetail> productsdDetails = productDetailRepository.findByIdProduct(idProduct);
        if (productsdDetails == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productsdDetails);
    }

    @GetMapping("fillVoucherPrice/{idProduct}")
    public ResponseEntity<List<Voucher>> fill(@PathVariable("idProduct") Integer idProduct) {
        List<Voucher> vouchers = voucherSellerRepository.findAllByIdProduct(idProduct);
        if (vouchers == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(vouchers);
    }

    // @GetMapping("fillVoucher/{idStore}")
    // public ResponseEntity<?> fillVoucher(@PathVariable Integer idStore,
    // @RequestParam("pageNo") Optional<Integer> pageNo,
    // @RequestParam("pageSize") Optional<Integer> pageSize,
    // @RequestParam(name = "keyWord", defaultValue = "") String keyWord,
    // @RequestParam(name = "status", defaultValue = "") String status,
    // @RequestParam(name = "sortBy", defaultValue = "") String sortBy) {

    // // Khởi tạo sort
    // Sort sort = Sort.by(Direction.DESC, "id");
    // switch (sortBy) {
    // case "newVouchers":
    // sort = Sort.by(Direction.DESC, "id");
    // break;
    // case "oldVouchers":
    // sort = Sort.by(Direction.ASC, "id");
    // break;
    // case "disCountPriceASC":
    // sort = Sort.by(Direction.ASC, "discountprice");
    // break;
    // case "disCountPriceDESC":
    // sort = Sort.by(Direction.DESC, "discountprice");
    // break;
    // default:
    // sort = Sort.by(Direction.DESC, "id");
    // break;
    // }
    // // Khỏi tạo pageable
    // Pageable pageable = PageRequest.of(pageNo.orElse(0), pageSize.orElse(10),
    // sort);
    // // Khởi tạo page;
    // Page<Voucher> vPage = null;

    // if (keyWord.isEmpty() && status.isEmpty()) {
    // vPage = voucherSellerRepository.findAllByIdStore(idStore, "%", "%",
    // pageable);
    // } else {
    // if (!keyWord.isEmpty() && status.isEmpty()) {
    // vPage = voucherSellerRepository.findAllByIdStore(idStore, "%" + keyWord +
    // "%", "%", pageable);
    // } else if (!status.isEmpty() && keyWord.isEmpty()) {
    // vPage = voucherSellerRepository.findAllByIdStore(idStore, "%", status,
    // pageable);
    // } else {
    // vPage = voucherSellerRepository.findAllByIdStore(idStore, "%" + keyWord +
    // "%", status,
    // pageable);
    // }

    // }

    // if (vPage == null) {
    // return ResponseEntity.notFound().build();
    // }

    // // Tạo 1 Map để trả về dữ liệu
    // Map<String, Object> response = new HashMap<>();
    // response.put("vouchers", vPage.getContent()); // Danh sách sản phẩm
    // response.put("currentPage", vPage.getNumber()); // Trang hiện tại
    // response.put("totalPage", vPage.getTotalPages()); // Tổng số trang
    // response.put("totalItem", vPage.getTotalElements());// Tổng số voucher
    // return ResponseEntity.ok(response);
    // }

    @GetMapping("fillVoucher/{idStore}")
    public ResponseEntity<?> fillVoucher(@PathVariable Integer idStore,
            @RequestParam("pageNo") Optional<Integer> pageNo,
            @RequestParam("pageSize") Optional<Integer> pageSize,
            @RequestParam(name = "keyWord", defaultValue = "") String keyWord,
            @RequestParam(name = "status", defaultValue = "") String status,
            @RequestParam(name = "sortBy", defaultValue = "") String sortBy) {

        // Truy vấn danh sách vouchername duy nhất
        List<String> voucherNames = voucherSellerRepository.findAllByIdStore(idStore, "%" + keyWord + "%",
                status.isEmpty() ? "%" : status);

        // Sắp xếp danh sách voucher theo id (tăng dần hoặc giảm dần)
        // Comparator<Voucher> comparator;
        // switch (sortBy) {
        //     case "newVouchers":
        //         comparator = Comparator.comparingInt(Voucher::getId).reversed(); // sắp xếp giảm dần theo id
        //         break;
        //     case "oldVouchers":
        //         comparator = Comparator.comparingInt(Voucher::getId); // sắp xếp tăng dần theo id
        //         break;
        //     case "disCountPriceASC":
        //         comparator = Comparator.comparingInt(Voucher::getDiscountprice); // sắp xếp theo giá trị giảm giá
        //                                                                             // tăng dần
        //         break;
        //     case "disCountPriceDESC":
        //         comparator = Comparator.comparingInt(Voucher::getDiscountprice).reversed(); // sắp xếp theo giá trị
        //                                                                                        // giảm giá giảm dần
        //         break;
        //     default:
        //         comparator = Comparator.comparingInt(Voucher::getId).reversed(); // mặc định sắp xếp giảm dần theo id
        // }

        // // Áp dụng sắp xếp
        // voucherNames.sort(comparator);

        // Phân trang thủ công
        int page = pageNo.orElse(0);
        int size = pageSize.orElse(10);
        int start = Math.min(page * size, voucherNames.size());
        int end = Math.min((page + 1) * size, voucherNames.size());
        List<String> paginatedVoucherNames = voucherNames.subList(start, end);

        // Lấy từng nhóm voucher theo namevoucher
        List<Map<String, Object>> vouchersGrouped = new ArrayList<>();
        for (String nameVoucher : paginatedVoucherNames) {
            List<Voucher> vouchers = voucherSellerRepository.findByIdStoreAndNameVoucher(idStore, nameVoucher);
            Map<String, Object> group = new HashMap<>();
            group.put("nameVoucher", nameVoucher);
            group.put("vouchers", vouchers);
            vouchersGrouped.add(group);
        }

        // Tạo 1 Map để trả về dữ liệu
        Map<String, Object> response = new HashMap<>();
        response.put("vouchersGrouped", vouchersGrouped); // Danh sách nhóm voucher
        response.put("currentPage", page); // Trang hiện tại
        response.put("totalPage", (int) Math.ceil((double) voucherNames.size() / size)); // Tổng số trang
        response.put("totalItem", voucherNames.size()); // Tổng số nhóm nameVoucher
        return ResponseEntity.ok(response);
    }

    @GetMapping("fillVoucherShop/{slug}")
    public ResponseEntity<List<Voucher>> fillVoucherShop(@PathVariable String slug) {
        List<Voucher> vouchers = voucherSellerRepository.findAllBySlugStore(slug);
        if (vouchers == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(vouchers);
    }

    @GetMapping("editVoucherShop/{slug}")
    public ResponseEntity<?> getVouchersByVoucherName(@PathVariable("slug") String slug)
            throws UnsupportedEncodingException {
        List<Voucher> vouchers = voucherSellerRepository.editVoucherBySlug("%" + slug + "%");
        if (vouchers == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(vouchers);
    }

    @PostMapping("addVouchers")
    public ResponseEntity<?> addVoucher(@RequestBody VoucherRequest voucherRequest) {

        // Kiểm tra trùng tên voucher hoặc id
        for (ProductDetail getById : voucherRequest.getProductDetails()) {
            Integer checkTrungNameVoucher = voucherSellerRepository.checkTrungNameVoucherAndIdProductDetail(
                    "%" + voucherRequest.getVoucher().getVouchername() + "%",
                    getById.getId());
            if (checkTrungNameVoucher > 0) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Tên voucher hoặc phân loại sản phẩm đã bị trùng.");
            }
        }

        // Lấy ngày hiện tại
        LocalDate currentDate = LocalDate.now();

        // Lặp qua các ProductDetail để thêm Voucher
        for (ProductDetail x : voucherRequest.getProductDetails()) {
            Voucher voucher = new Voucher();
            voucher.setVouchername(voucherRequest.getVoucher().getVouchername());
            voucher.setProductDetail(x);
            voucher.setDiscountprice(voucherRequest.getVoucher().getDiscountprice());
            voucher.setStartday(voucherRequest.getVoucher().getStartday());
            voucher.setEndday(voucherRequest.getVoucher().getEndday());

            // Chuyển đổi startday từ Date sang LocalDate
            LocalDate startDay = voucherRequest.getVoucher().getStartday().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            // Kiểm tra startday và cập nhật status
            if (startDay.isEqual(currentDate)) {
                voucher.setStatus("Hoạt động");
            } else {
                voucher.setStatus("Chờ hoạt động");
            }

            // Nếu slug trống, tạo slug mới
            if (voucherRequest.getVoucher().getSlug().isEmpty()) {
                voucher.setSlug(slugText.generateUniqueSlug(voucherRequest.getVoucher().getVouchername()));
            }

            // Lưu voucher vào database
            voucherSellerRepository.save(voucher);
        }

        return ResponseEntity.ok("Thêm voucher thành công");
    }

    @PutMapping("updateVoucher/{slug}")
    public ResponseEntity<?> updateVoucher(@PathVariable String slug,
            @RequestBody VoucherRequest voucherRequest) {
        // Tìm kiếm voucher theo slug
        List<Voucher> existingVouchers = voucherSellerRepository.findBySlug(slug);
        if (existingVouchers.isEmpty()) {
            return ResponseEntity.notFound().build(); // Nếu không tìm thấy, trả về 404
        }

        // Kiểm tra trùng tên voucher hoặc id
        List<Voucher> vouchers = voucherSellerRepository
                .checkTrungVoucher(voucherRequest.getVoucher().getVouchername());
        for (ProductDetail getById : voucherRequest.getProductDetails()) {
            Integer checkTrungNameVoucher = voucherSellerRepository.checkTrungNameVoucherAndIdProductDetail(
                    "%" + voucherRequest.getVoucher().getVouchername() + "%",
                    getById.getId());
            if (checkTrungNameVoucher > vouchers.size()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Tên voucher hoặc phân loại sản phẩm đã bị trùng.");
            }
        }

        // Lấy ngày hiện tại
        LocalDate currentDate = LocalDate.now();

        // Lấy danh sách voucher mới từ voucherRequest
        List<ProductDetail> productDetails = voucherRequest.getProductDetails();
        int sizeDifference = existingVouchers.size() - productDetails.size();

        // Nếu số lượng voucher trong cơ sở dữ liệu nhiều hơn, xóa các voucher không còn
        // cần thiết
        if (sizeDifference > 0) {
            // Xóa bớt voucher thừa
            for (int i = existingVouchers.size() - 1; i >= productDetails.size(); i--) {
                voucherSellerRepository.delete(existingVouchers.get(i)); // Xóa voucher thừa
            }
        }

        // Cập nhật hoặc thêm mới voucher
        for (int i = 0; i < productDetails.size(); i++) {
            Voucher saved;
            if (i < existingVouchers.size()) {
                saved = existingVouchers.get(i); // Cập nhật voucher đã tồn tại
            } else {
                saved = new Voucher(); // Tạo mới voucher nếu không có sẵn
                saved.setSlug(slug); // Đảm bảo slug được giữ nguyên
            }

            // Cập nhật các giá trị voucher từ voucherRequest
            saved.setVouchername(voucherRequest.getVoucher().getVouchername());
            saved.setDiscountprice(voucherRequest.getVoucher().getDiscountprice());
            // Chuyển đổi startday từ Date sang LocalDate
            LocalDate startDay = voucherRequest.getVoucher().getStartday().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            // Kiểm tra startday và cập nhật status
            if (startDay.isEqual(currentDate)) {
                saved.setStatus("Hoạt động");
            } else {
                saved.setStatus("Chờ hoạt động");
            }
            saved.setStartday(voucherRequest.getVoucher().getStartday());
            saved.setEndday(voucherRequest.getVoucher().getEndday());

            // Cập nhật ProductDetail cho voucher
            ProductDetail productDetail = productDetails.get(i);
            saved.setProductDetail(productDetail);

            // Lưu lại voucher đã cập nhật hoặc tạo mới
            voucherSellerRepository.save(saved);
        }

        // Trả về phản hồi thành công
        return ResponseEntity.ok("Cập nhật thành công");
    }

    @DeleteMapping("delete/{slug}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable String slug) {
        if (voucherSellerRepository.existsBySlug(slug)) {
            voucherSellerRepository.deleteBySlug(slug);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
