package com.duantn.be_project.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.duantn.be_project.Repository.IamgesReportRepository;
import com.duantn.be_project.Repository.OrderRepository;
import com.duantn.be_project.Repository.ProductRepository;
import com.duantn.be_project.Repository.ReportReponsitory;
import com.duantn.be_project.Repository.StoreRepository;
import com.duantn.be_project.Service.FirebaseStorageService;
import com.duantn.be_project.model.ImagesReport;
import com.duantn.be_project.model.Order;
import com.duantn.be_project.model.Product;
import com.duantn.be_project.model.Report;
import com.duantn.be_project.model.Store;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@CrossOrigin("*")
public class ReportController {
    @Autowired
    ReportReponsitory reportReponsitory;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    IamgesReportRepository iamgesReportRepository;
    @Autowired
    FirebaseStorageService firebaseStorageService;

    @PreAuthorize("hasAnyAuthority('Buyer_Manage_Buyer', 'Seller_Manage_Shop')")
    @GetMapping("/report/list/wait/{idUser}")
    public ResponseEntity<?> getListReportWait(
            @PathVariable("idUser") Integer idUser,
            @RequestParam("pageNo") Optional<Integer> pageNo,
            @RequestParam("pageSize") Optional<Integer> pageSize) {
        // Khởi tạo sort;
        Sort sort = Sort.by(Direction.DESC, "id");
        // Khởi tạo pageable
        Pageable pageable = PageRequest.of(pageNo.orElse(0), pageSize.orElse(5), sort);
        Page<Report> prReprot = reportReponsitory.listReportByStatus(idUser, "Chờ xử lý", pageable);

        // Tạo một Map để trả dữ liệu
        Map<String, Object> response = new HashMap<>();
        response.put("reports", prReprot.getContent()); // Danh sách sản phẩm phân trang
        response.put("currentPage", prReprot.getNumber()); // Trang hiện tại
        response.put("totalPage", prReprot.getTotalPages()); // Tổng số trang
        response.put("totalItems", prReprot.getTotalElements()); // Tổng số sản phẩm
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('Buyer_Manage_Buyer', 'Seller_Manage_Shop')")
    @GetMapping("/report/list/process/{idUser}")
    public ResponseEntity<?> getListReportProcess(
            @PathVariable("idUser") Integer idUser,
            @RequestParam("pageNo") Optional<Integer> pageNo,
            @RequestParam("pageSize") Optional<Integer> pageSize) {
        // Khởi tạo sort;
        Sort sort = Sort.by(Direction.DESC, "id");
        // Khởi tạo pageable
        Pageable pageable = PageRequest.of(pageNo.orElse(0), pageSize.orElse(5), sort);
        Page<Report> prReprot = reportReponsitory.listReportByStatus(idUser, "Đã xử lý", pageable);

        // Tạo một Map để trả dữ liệu
        Map<String, Object> response = new HashMap<>();
        response.put("reports", prReprot.getContent()); // Danh sách sản phẩm phân trang
        response.put("currentPage", prReprot.getNumber()); // Trang hiện tại
        response.put("totalPage", prReprot.getTotalPages()); // Tổng số trang
        response.put("totalItems", prReprot.getTotalElements()); // Tổng số sản phẩm
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('Buyer_Manage_Buyer', 'Seller_Manage_Shop')")
    @GetMapping("/report/list/refuse/{idUser}")
    public ResponseEntity<?> getListReportRefuse(
            @PathVariable("idUser") Integer idUser,
            @RequestParam("pageNo") Optional<Integer> pageNo,
            @RequestParam("pageSize") Optional<Integer> pageSize) {
        // Khởi tạo sort;
        Sort sort = Sort.by(Direction.DESC, "id");
        // Khởi tạo pageable
        Pageable pageable = PageRequest.of(pageNo.orElse(0), pageSize.orElse(5), sort);
        Page<Report> prReprot = reportReponsitory.listReportByStatus(idUser, "Từ chối xử lý", pageable);

        // Tạo một Map để trả dữ liệu
        Map<String, Object> response = new HashMap<>();
        response.put("reports", prReprot.getContent()); // Danh sách sản phẩm phân trang
        response.put("currentPage", prReprot.getNumber()); // Trang hiện tại
        response.put("totalPage", prReprot.getTotalPages()); // Tổng số trang
        response.put("totalItems", prReprot.getTotalElements()); // Tổng số sản phẩm
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('Buyer_Manage_Buyer', 'Seller_Manage_Shop')")
    @GetMapping("/report/{id}")
    public ResponseEntity<Report> getListReportWait(
            @PathVariable("id") Integer id) {
        Report report = reportReponsitory.findById(id).orElse(null);
        if (report.getId() == null || report == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(report);
    }

    @PreAuthorize("hasAnyAuthority('Admin_All_Function', 'Admin_Manage_Support')")
    @GetMapping("/report/list")
    public ResponseEntity<?> getListReport(
            @RequestParam("pageNo") Optional<Integer> pageNo,
            @RequestParam("pageSize") Optional<Integer> pageSize,
            @RequestParam(name = "keySort", defaultValue = "") String keySort) {
        Sort sort = Sort.by(Direction.DESC, "id");
        Pageable pageable = PageRequest.of(pageNo.orElse(0), pageSize.orElse(20), sort);
        Page<Report> prReport = null;

        // Kiểm tra keySort
        if (!keySort.isEmpty() && keySort != null) {
            prReport = reportReponsitory.listAllReportByStatus(keySort, pageable);
        } else {
            prReport = reportReponsitory.listAllReportByStatus("%", pageable);
        }
        // Tạo một Map để trả về dữ liệu
        Map<String, Object> response = new HashMap<>();
        response.put("reports", prReport.getContent()); // Danh sách sản phẩm
        response.put("currentPage", prReport.getNumber()); // Trang hiện tại (hiển
        // thị từ 1)
        response.put("totalPages", prReport.getTotalPages()); // Tổng số trang
        response.put("totalItems", prReport.getTotalElements()); // Tổng số sản phẩm
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('Buyer_Manage_Buyer', 'Seller_Manage_Shop')")
    @PostMapping("/report/create")
    public ResponseEntity<?> createReport(
            @RequestPart("report") String reportJson,
            @RequestPart("files") MultipartFile[] files) {
        try {
            // Chuyển JSON thành đối tượng Report
            ObjectMapper mapper = new ObjectMapper();
            Report report = mapper.readValue(reportJson, Report.class);

            // Kiểm tra và lấy Product từ cơ sở dữ liệu
            if (report.getProduct() != null && report.getProduct().getId() != null) {
                Product product = productRepository.findById(report.getProduct().getId()).orElse(null);
                report.setProduct(product); // Nếu không tìm thấy, set product là null
            } else {
                report.setOrder(null); // Nếu không có ID, set product là null
            }
            // Kiểm tra và lấy order từ cơ sở dữ liệu
            if (report.getOrder() != null && report.getOrder().getId() != null) {
                Order order = orderRepository.findById(report.getOrder().getId()).orElse(null);
                report.setOrder(order); // Nếu không tìm thấy, set product là null
            } else {
                report.setOrder(null); // Nếu không có ID, set product là null
            }

            // Kiểm tra và lấy Store từ cơ sở dữ liệu
            if (report.getStore() != null && report.getStore().getId() != null) {
                Store store = storeRepository.findById(report.getStore().getId()).orElse(null);
                report.setStore(store); // Nếu không tìm thấy, set store là null
            } else {
                report.setStore(null); // Nếu không có ID, set store là null
            }

            report.setReplyreport(null);
            report.setCreatedat(LocalDateTime.now());
            Report savedReport = reportReponsitory.save(report);
            // Xử lý file video
            List<String> imageReportUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                try {
                    String media = firebaseStorageService.uploadToFirebase(file, "reports");
                    if (media != null) {
                        imageReportUrls.add(media);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to upload image: " + e.getMessage());
                }
            }
            // Tạo các đối tượng IamgesReport liên kết với Report
            List<ImagesReport> imagesReports = new ArrayList<>();
            for (String mediaFile : imageReportUrls) {
                ImagesReport imagesReport = new ImagesReport();
                imagesReport.setMedia(mediaFile);
                imagesReport.setReport(savedReport);
                imagesReports.add(imagesReport);
            }

            // Lưu danh sách ảnh
            iamgesReportRepository.saveAll(imagesReports);

            return ResponseEntity.ok("Báo cáo thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi xử lý báo cáo: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('Admin_All_Function', 'Admin_Manage_Support')")
    @PutMapping("/report/update/{id}")
    public ResponseEntity<?> updateReport(@PathVariable("id") Integer id,
            @RequestBody Report reportRequest) {
        Report report = reportReponsitory.findById(id).orElse(null);
        if (report.getId() == null || report == null) {
            return ResponseEntity.notFound().build();
        }
      
        report.setStatus(reportRequest.getStatus());
        report.setCreatedat(reportRequest.getCreatedat());
        report.setReplyreport(reportRequest.getReplyreport());

        Report savedReport = reportReponsitory.save(report);

        return ResponseEntity.ok(savedReport);
    }

}
