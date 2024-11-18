package com.duantn.be_project.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.util.stream.Collectors;

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
import org.springframework.web.multipart.MultipartFile;

import com.duantn.be_project.Repository.CategoryRepository;
import com.duantn.be_project.Repository.ImageRepository;
import com.duantn.be_project.Repository.OrderRepository;
import com.duantn.be_project.Repository.ProductDetailRepository;
import com.duantn.be_project.Repository.ProductRepository;
import com.duantn.be_project.Repository.StoreRepository;
import com.duantn.be_project.Repository.TradeMarkRepository;
import com.duantn.be_project.Service.SlugText.SlugText;
import com.duantn.be_project.model.Image;
import com.duantn.be_project.model.Product;
import com.duantn.be_project.model.ProductDetail;
import com.duantn.be_project.model.Store;
import com.duantn.be_project.model.Request_Response.ProductDTO;
import com.duantn.be_project.untils.FileManagerService;
import com.duantn.be_project.untils.UploadImages;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletContext;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin("*")
@RestController
public class ProductController {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    TradeMarkRepository tradeMarkRepository;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    FileManagerService fileManagerService;
    @Autowired
    ServletContext servletContext;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductDetailRepository productDetailRepository;
    @Autowired
    UploadImages uploadImages;
    @Autowired
    SlugText slugText;

    // Tìm kiếm và phân trang
    @GetMapping("/home/product/list")
    public ResponseEntity<?> List(
            @RequestParam("pageNo") Optional<Integer> pageNo,
            @RequestParam("pageSize") Optional<Integer> pageSize,
            @RequestParam(name = "keyWord", defaultValue = "") String keyWord) {

        // Tách từ khóa thành mảng các chuỗi con
        String[] keywords = keyWord.split(" ");

        // Khởi tạo danh sách để lưu các chuỗi hợp lệ
        List<String> listSearch = new ArrayList<>();

        // Kiểm tra từng từ khóa trong mảng keywords
        Arrays.stream(keywords).forEach(string -> {
            Boolean checkCate = categoryRepository.checkEmptyCategory("%" + string.split(" ") + "%");
            Boolean checkTradeMark = tradeMarkRepository.checkEmptyTradeMark("%" + string.split(" ") + "%");

            // Nếu tồn tại trong category hoặc trademark, thêm từ khóa vào listSearch
            if (checkCate || checkTradeMark) {
                listSearch.add(string);
            }
        });

        // In ra danh sách các từ khóa hợp lệ để kiểm tra
        // System.out.println("Các từ khóa hợp lệ: " + listSearch);

        // Khởi tạo biến sắp xếp
        Sort sort;
        // Khởi tại biến phân trang
        Pageable pageable;
        // khởi tạo biến chứa sản phẩm phân trang
        Page<Object[]> prPage = null;
        // Khởi tạo biến kiểm tra idCate
        Integer idCate = null;

        // Kiểm tra keyWord
        if (keyWord.toLowerCase().matches(".*\\bhot\\b.*") || keyWord.toLowerCase().contains("yêu thích")
                || keyWord.toLowerCase().contains("bán chạy") ||
                keyWord.toLowerCase().contains("phổ biến") || keyWord.toLowerCase().contains("được ưa chuộng")
                || keyWord.toLowerCase().contains("hàng đầu")
                || keyWord.toLowerCase().contains("nổi bật")
                || keyWord.toLowerCase().contains("xu hướng") ||
                keyWord.toLowerCase().contains("top")
                || keyWord.toLowerCase().contains("săn đón") ||
                keyWord.toLowerCase().contains("được quan tâm")
                || keyWord.toLowerCase().contains("bán nhiều") ||
                keyWord.toLowerCase().contains("best seller") ||
                keyWord.toLowerCase().contains("bestseller") ||
                keyWord.toLowerCase().contains("best-seller") ||
                keyWord.toLowerCase().contains("được đánh giá cao") ||
                keyWord.toLowerCase().contains("được mua nhiều")) {
            sort = Sort.by(Direction.DESC, "orderCount");

            pageable = PageRequest.of(pageNo.orElse(0), pageSize.orElse(20),
                    sort);

            prPage = productRepository.findByNamePrCateTrademark(
                    "%",
                    "%",
                    "%",
                    "%",
                    "%",
                    null,
                    pageable);
        } else {
            sort = Sort.by(Direction.DESC, "p.id"); // Giảm dần theo cột id
            pageable = PageRequest.of(pageNo.orElse(0), pageSize.orElse(20),
                    sort);

            // Kiểm tra nếu keyword có thể chuyển đổi thành số
            if (!keyWord.isEmpty() && listSearch.size() > 0) {
                try {
                    // Tìm theo category ID danh mục
                    idCate = Integer.parseInt(keyWord);
                    prPage = productRepository.findByNamePrCateTrademark("", "", "", "", "", idCate, pageable);

                } catch (NumberFormatException e) {
                    // Nếu keyword không phải là số thì tìm theo tên hoặc danh mục
                    // Đảm bảo listSearch có đủ 5 phần tử
                    while (listSearch.size() < 5) {
                        listSearch.add(""); // Thêm phần tử rỗng nếu kích thước nhỏ hơn 5
                    }

                    // Tạo một mảng để lưu các tham số cho hàm tìm kiếm
                    String[] searchParams = new String[5];

                    for (int i = 0; i < searchParams.length; i++) {
                        // Kiểm tra xem phần tử có phải là chuỗi không rỗng hay không
                        if (i < listSearch.size() && !listSearch.get(i).isEmpty()) {
                            searchParams[i] = "%" + listSearch.get(i) + "%"; // Thêm ký tự '%' nếu không rỗng
                        } else {
                            searchParams[i] = ""; // Nếu phần tử rỗng, thêm chuỗi rỗng
                        }
                    }

                    // Gọi hàm tìm kiếm với các tham số đã xử lý
                    prPage = productRepository.findByNamePrCateTrademark(
                            searchParams[0],
                            searchParams[1],
                            searchParams[2],
                            searchParams[3],
                            searchParams[4],
                            null,
                            pageable);

                }
            } else {
                prPage = productRepository.findByNamePrCateTrademark(
                        "%",
                        "%",
                        "%",
                        "%",
                        "%",
                        null,
                        pageable);
            }

        }

        // Cắt danh sách trước khi gửi lên client
        List<Product> products = prPage.getContent().stream().map((sliceElement) -> (Product) sliceElement[0])
                .collect(Collectors.toList());

        List<ProductDTO> productDTOs = products.stream().map(product -> {
            List<ProductDetail> productDetails = productDetailRepository.findByIdProduct(product.getId());
            Integer countOrderSuccess = productDetails.stream()
                    .mapToInt((detailProduct) -> orderRepository.countOrderBuyed(detailProduct.getId())).sum();
            return new ProductDTO(product, productDetails, countOrderSuccess);
        }).collect(Collectors.toList());

        // Tạo một Map để trả về dữ liệu
        Map<String, Object> response = new HashMap<>();
        response.put("products", productDTOs); // Danh sách sản phẩm
        response.put("currentPage", prPage.getNumber() + 1); // Trang hiện tại (hiển
        // thị từ 1)
        response.put("totalPages", prPage.getTotalPages()); // Tổng số trang
        response.put("totalItems", prPage.getTotalElements()); // Tổng số sản phẩm

        return ResponseEntity.ok(response);
    }

    @GetMapping("/findMore/{name}")
    public ResponseEntity<?> findMoreProducts(@PathVariable("name") String name,
            @RequestParam("pageNo") Optional<Integer> pageNo,
            @RequestParam("pageSize") Optional<Integer> pageSize,
            @RequestParam(name = "keyWord", defaultValue = "") String keyWord,
            @RequestParam(name = "sortBy", defaultValue = "") String sortBy,
            @RequestParam(name = "minPriceSlider", defaultValue = "") String minPriceSlider,
            @RequestParam(name = "maxPriceSlider", defaultValue = "") String maxPriceSlider,
            @RequestParam(name = "shopType", defaultValue = "") String shopTypeString,
            @RequestParam(name = "tradeMark", defaultValue = "") String tradeMarkString)
            throws UnsupportedEncodingException {

        // Tách từ khóa thành mảng các chuỗi con
        String[] keywords = keyWord.split(" ");

        // Khởi tạo danh sách để lưu các chuỗi hợp lệ
        List<String> listSearch = new ArrayList<>();

        // Kiểm tra từng từ khóa trong mảng keywords
        Arrays.stream(keywords).forEach(string -> {
            Boolean checkCate = categoryRepository.checkEmptyCategory("%" + string.split(" ") + "%");
            Boolean checkTradeMark = tradeMarkRepository.checkEmptyTradeMark("%" + string.split(" ") + "%");

            // Nếu tồn tại trong category hoặc trademark, thêm từ khóa vào listSearch
            if (checkCate || checkTradeMark) {
                listSearch.add(string);
            }
        });

        // In ra danh sách các từ khóa hợp lệ để kiểm tra
        // System.out.println("Các từ khóa hợp lệ: " + listSearch);

        // Thiết lập Sort dựa vào yêu cầu của người dùng
        Sort sort = Sort.by(Direction.DESC, "p.id");
        switch (sortBy) {
            case "newItems":
                sort = Sort.by(Direction.DESC, "p.id");
                break;
            case "oldItems":
                sort = Sort.by(Direction.ASC, "p.id");
                break;
            case "priceASC":
                sort = Sort.by(Direction.ASC, "maxPrice");
                break;
            case "priceDESC":
                sort = Sort.by(Direction.DESC, "maxPrice");
                break;
            case "bestSeller":
                sort = Sort.by(Direction.DESC, "orderCount");
                break;
            default:
                sort = Sort.by(Direction.DESC, "p.id");
                break;
        }

        String decodeName = URLDecoder.decode(name, StandardCharsets.UTF_8.name());

        // Lấy minPrice và maxPrice từ cơ sở dữ liệu nếu không có giá trị từ request
        List<Object[]> minMaxPrice = productDetailRepository.minMaxPriceDetail("%" + decodeName + "%");
        Object[] prices = minMaxPrice.get(0);

        // Kiểm tra nếu giá trị minPriceSlider và maxPriceSlider là chuỗi rỗng
        Integer minPrice = minPriceSlider.isEmpty() ? ((Number) prices[0]).intValue() : Integer.valueOf(minPriceSlider);
        Integer maxPrice = maxPriceSlider.isEmpty() ? ((Number) prices[1]).intValue() : Integer.valueOf(maxPriceSlider);

        // Ép shopType về kiểu list để truyền vào querry
        List<Integer> shopType = shopTypeString.isEmpty() ? null
                : Arrays.stream(shopTypeString.split(","))
                        .map(Integer::valueOf)
                        .collect(Collectors.toList());

        // Ép tradeMark về kiểu list để truyền vào querry
        // Chuyển đổi chuỗi tradeMarkString thành danh sách
        List<String> tradeMark = tradeMarkString.isEmpty()
                ? null
                : Arrays.stream(tradeMarkString.split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());

        Pageable pageable;
        Page<Object[]> prPage = null;
        List<Object[]> fullList = null;

        // Kiểm tra keyWord
        if (keyWord.toLowerCase().matches(".*\\bhot\\b.*") || keyWord.toLowerCase().contains("yêu thích")
                || keyWord.toLowerCase().contains("bán chạy") ||
                keyWord.toLowerCase().contains("phổ biến") || keyWord.toLowerCase().contains("được ưa chuộng")
                || keyWord.toLowerCase().contains("hàng đầu")
                || keyWord.toLowerCase().contains("nổi bật")
                || keyWord.toLowerCase().contains("xu hướng") ||
                keyWord.toLowerCase().contains("top")
                || keyWord.toLowerCase().contains("săn đón") ||
                keyWord.toLowerCase().contains("được quan tâm")
                || keyWord.toLowerCase().contains("bán nhiều") ||
                keyWord.toLowerCase().contains("best seller") ||
                keyWord.toLowerCase().contains("bestseller") ||
                keyWord.toLowerCase().contains("best-seller") ||
                keyWord.toLowerCase().contains("được đánh giá cao") ||
                keyWord.toLowerCase().contains("được mua nhiều")) {
            sort = Sort.by(Direction.DESC, "orderCount");

            pageable = PageRequest.of(pageNo.orElse(0), pageSize.orElse(20),
                    sort);

            prPage = productRepository.queryFindMore(decodeName,
                    "%",
                    "%",
                    "%",
                    "%",
                    "%",
                    minPrice, maxPrice, shopType, tradeMark,
                    pageable);
            // Gọi truy vấn không phân trang để lấy toàn bộ danh sách
            fullList = productRepository.queryFindMoreFullList(decodeName,
                    keyWord.isEmpty() ? "%" : "%" + keyWord + "%", minPrice, maxPrice, shopType, tradeMark, sort);
        } else {
            pageable = PageRequest.of(pageNo.orElse(0), pageSize.orElse(20),
                    sort);

            // Kiểm tra nếu keyword và mảng listSearch
            if (!keyWord.isEmpty() && listSearch.size() > 0) {
                // Đảm bảo listSearch có đủ 5 phần tử
                while (listSearch.size() < 5) {
                    listSearch.add(""); // Thêm phần tử rỗng nếu kích thước nhỏ hơn 5
                }

                // Tạo một mảng để lưu các tham số cho hàm tìm kiếm
                String[] searchParams = new String[5];

                for (int i = 0; i < searchParams.length; i++) {
                    // Kiểm tra xem phần tử có phải là chuỗi không rỗng hay không
                    if (i < listSearch.size() && !listSearch.get(i).isEmpty()) {
                        searchParams[i] = "%" + listSearch.get(i) + "%"; // Thêm ký tự '%' nếu không rỗng

                    } else {
                        searchParams[i] = ""; // Nếu phần tử rỗng, thêm chuỗi rỗng
                    }
                }

                // Gọi truy vấn
                prPage = productRepository.queryFindMore(decodeName,
                        searchParams[0],
                        searchParams[1],
                        searchParams[2],
                        searchParams[3],
                        searchParams[4],
                        minPrice, maxPrice, shopType, tradeMark,
                        pageable);
                // Gọi truy vấn không phân trang để lấy toàn bộ danh sách
                fullList = productRepository.queryFindMoreFullList(decodeName,
                        keyWord.isEmpty() ? "%" : "%" + keyWord + "%", minPrice, maxPrice, shopType,
                        tradeMark, sort);
            } else {
                // Gọi truy vấn
                prPage = productRepository.queryFindMore(decodeName,
                        "%",
                        "%",
                        "%",
                        "%",
                        "%",
                        minPrice, maxPrice, shopType, tradeMark,
                        pageable);
                // Gọi truy vấn không phân trang để lấy toàn bộ danh sách
                fullList = productRepository.queryFindMoreFullList(decodeName,
                        keyWord.isEmpty() ? "%" : "%" + keyWord + "%", minPrice, maxPrice, shopType,
                        tradeMark, sort);
            }

        }

        // Cắt danh sách trước khi gửi lên client
        List<Product> products = prPage.getContent().stream().map((sliceElement) -> (Product) sliceElement[0])
                .collect(Collectors.toList());

        // Hiển thị full list product
        List<Product> fullListPr = fullList.stream().map((sliceElement) -> (Product) sliceElement[0])
                .collect(Collectors.toList());

        // Truy vấn từng id của prPage
        List<ProductDTO> productDTOs = products.stream().map(product -> {
            List<ProductDetail> productDetails = productDetailRepository.findByIdProduct(product.getId());
            Integer countOrderSuccess = productDetails.stream()
                    .mapToInt((detailProduct) -> orderRepository.countOrderBuyed(detailProduct.getId())).sum();
            return new ProductDTO(product, productDetails, countOrderSuccess);
        }).collect(Collectors.toList());

        // Truy vấn từng id của fullList
        List<ProductDTO> productDTOFullLists = fullListPr.stream().map(product -> {
            List<ProductDetail> productDetails = productDetailRepository.findByIdProduct(product.getId());
            Integer countOrderSuccess = productDetails.stream()
                    .mapToInt((detailProduct) -> orderRepository.countOrderBuyed(detailProduct.getId())).sum();
            return new ProductDTO(product, productDetails, countOrderSuccess);
        }).collect(Collectors.toList());

        // Tạo một Map để trả dữ liệu
        Map<String, Object> response = new HashMap<>();
        response.put("products", productDTOs); // Danh sách sản phẩm phân trang
        response.put("fullListProduct", productDTOFullLists); // Danh sách toàn bộ sản phẩm
        response.put("currentPage", prPage.getNumber() + 1); // Trang hiện tại
        response.put("totalPage", prPage.getTotalPages()); // Tổng số trang
        response.put("totalItems", prPage.getTotalElements()); // Tổng số sản phẩm
        return ResponseEntity.ok(response);
    }

    // Api danh sách sản phẩm có store taxcode
    @GetMapping("productPerMall/list")
    public ResponseEntity<?> productPerMall(@RequestParam("pageNo") Optional<Integer> pageNo,
            @RequestParam("pageSize") Optional<Integer> pageSize) {
        Sort sort = Sort.by(Direction.DESC, "id"); // Sắp xếp giảm dần
        Pageable pageable = PageRequest.of(pageNo.orElse(0), pageSize.orElse(8), sort);
        Page<Object[]> prPage = productRepository.listProductPerMall("%", "%", "%", "%", "%",
                pageable);

        // Map dữ liệu sản phẩm ra
        List<Product> products = prPage.getContent().stream().map((sliceElement) -> (Product) sliceElement[0])
                .collect(Collectors.toList());

        // Truy vấn từng id
        List<ProductDTO> productDTOs = products.stream().map(product -> {
            List<ProductDetail> productDetails = productDetailRepository.findByIdProduct(product.getId());
            Integer countOrderSuccess = productDetails.stream()
                    .mapToInt((detailProduct) -> orderRepository.countOrderBuyed(detailProduct.getId())).sum();
            return new ProductDTO(product, productDetails, countOrderSuccess);
        }).collect(Collectors.toList());

        // Tạo map để trả dữ liệu
        Map<String, Object> response = new HashMap<>();
        response.put("products", productDTOs); // Danh sách phân trang sản phẩm
        response.put("currentPage", prPage.getNumber()); // Số trang hiện tại
        response.put("totalPage", prPage.getTotalPages());// Tổng số trang
        response.put("totalItems", prPage.getTotalElements()); // Tổng số sản phẩm
        return ResponseEntity.ok(response);
    }

    @GetMapping("findMore/productPerMall/list")
    public ResponseEntity<?> productPerMallFindMorEntity(@RequestParam("pageNo") Optional<Integer> pageNo,
            @RequestParam("pageSize") Optional<Integer> pageSize,
            @RequestParam(name = "keyWord", defaultValue = "") String keyWord,
            @RequestParam(name = "sortBy", defaultValue = "") String sortBy) {

        // Tách từ khóa thành mảng các chuỗi con
        String[] keywords = keyWord.split(" ");

        // Khởi tạo danh sách để lưu các chuỗi hợp lệ
        List<String> listSearch = new ArrayList<>();

        // Kiểm tra từng từ khóa trong mảng keywords
        Arrays.stream(keywords).forEach(string -> {
            Boolean checkCate = categoryRepository.checkEmptyCategory("%" + string.split(" ") + "%");
            Boolean checkTradeMark = tradeMarkRepository.checkEmptyTradeMark("%" + string.split(" ") + "%");

            // Nếu tồn tại trong category hoặc trademark, thêm từ khóa vào listSearch
            if (checkCate || checkTradeMark) {
                listSearch.add(string);
            }
        });

        // In ra danh sách các từ khóa hợp lệ để kiểm tra
        // System.out.println("Các từ khóa hợp lệ: " + listSearch);

        // Thiết lập Sort dựa vào yêu cầu của người dùng
        Sort sort = Sort.by(Direction.DESC, "p.id");
        switch (sortBy) {
            case "newItems":
                sort = Sort.by(Direction.DESC, "p.id");
                break;
            case "oldItems":
                sort = Sort.by(Direction.ASC, "p.id");
                break;
            case "priceASC":
                sort = Sort.by(Direction.ASC, "maxPrice");
                break;
            case "priceDESC":
                sort = Sort.by(Direction.DESC, "maxPrice");
                break;
            case "bestSeller":
                sort = Sort.by(Direction.DESC, "orderCount");
                break;
            default:
                sort = Sort.by(Direction.DESC, "p.id");
                break;
        }

        Pageable pageable = PageRequest.of(pageNo.orElse(0), pageSize.orElse(20), sort);
        Page<Object[]> prPage;

        // Kiểm tra keyWord
        if (keyWord.toLowerCase().matches(".*\\bhot\\b.*") || keyWord.toLowerCase().contains("yêu thích")
                || keyWord.toLowerCase().contains("bán chạy") ||
                keyWord.toLowerCase().contains("phổ biến") || keyWord.toLowerCase().contains("được ưa chuộng")
                || keyWord.toLowerCase().contains("hàng đầu")
                || keyWord.toLowerCase().contains("nổi bật")
                || keyWord.toLowerCase().contains("xu hướng") ||
                keyWord.toLowerCase().contains("top")
                || keyWord.toLowerCase().contains("săn đón") ||
                keyWord.toLowerCase().contains("được quan tâm")
                || keyWord.toLowerCase().contains("bán nhiều") ||
                keyWord.toLowerCase().contains("best seller") ||
                keyWord.toLowerCase().contains("bestseller") ||
                keyWord.toLowerCase().contains("best-seller") ||
                keyWord.toLowerCase().contains("được đánh giá cao") ||
                keyWord.toLowerCase().contains("được mua nhiều")) {
            sort = Sort.by(Direction.DESC, "orderCount");

            pageable = PageRequest.of(pageNo.orElse(0), pageSize.orElse(20),
                    sort);

            prPage = productRepository.listProductPerMall(
                    "%",
                    "%",
                    "%",
                    "%",
                    "%",
                    pageable);
        } else {
            pageable = PageRequest.of(pageNo.orElse(0), pageSize.orElse(20),
                    sort);

            // Kiểm tra nếu keyword và listSearch
            if (!keyWord.isEmpty() && listSearch.size() > 0) {
                // Đảm bảo listSearch có đủ 5 phần tử
                while (listSearch.size() < 5) {
                    listSearch.add(""); // Thêm phần tử rỗng nếu kích thước nhỏ hơn 5
                }

                // Tạo một mảng để lưu các tham số cho hàm tìm kiếm
                String[] searchParams = new String[5];

                for (int i = 0; i < searchParams.length; i++) {
                    // Kiểm tra xem phần tử có phải là chuỗi không rỗng hay không
                    if (i < listSearch.size() && !listSearch.get(i).isEmpty()) {
                        searchParams[i] = "%" + listSearch.get(i) + "%"; // Thêm ký tự '%' nếu không rỗng
                    } else {
                        searchParams[i] = ""; // Nếu phần tử rỗng, thêm chuỗi rỗng
                    }
                }

                // Gọi hàm tìm kiếm với các tham số đã xử lý
                prPage = productRepository.listProductPerMall(
                        searchParams[0],
                        searchParams[1],
                        searchParams[2],
                        searchParams[3],
                        searchParams[4],
                        pageable);

            } else {
                prPage = productRepository.listProductPerMall(
                        "%",
                        "%",
                        "%",
                        "%",
                        "%",
                        pageable);
            }

        }

        // Duyệt prPage chỉ lấy thuộc tính product
        List<Product> products = prPage.getContent().stream().map((sliceElement) -> (Product) sliceElement[0])
                .collect(Collectors.toList());

        // Truy vấn từng id
        List<ProductDTO> productDTOs = products.stream().map(product -> {
            List<ProductDetail> productDetails = productDetailRepository.findByIdProduct(product.getId());
            Integer countOrderSuccess = productDetails.stream()
                    .mapToInt((detailProduct) -> orderRepository.countOrderBuyed(detailProduct.getId())).sum();
            return new ProductDTO(product, productDetails, countOrderSuccess);
        }).collect(Collectors.toList());

        // Tạo map để trả dữ liệu
        Map<String, Object> response = new HashMap<>();
        response.put("products", productDTOs); // Danh sách phân trang sản phẩm
        response.put("currentPage", prPage.getNumber() + 1); // Số trang hiện tại
        response.put("totalPage", prPage.getTotalPages());// Tổng số trang
        response.put("totalItems", prPage.getTotalElements()); // Tổng số sản phẩm
        return ResponseEntity.ok(response);
    }

    // GetAllByIdStore
    @GetMapping("/productStore/{slug}")
    public ResponseEntity<?> getStoreBySlugStore(
            @PathVariable("slug") String slug,
            @RequestParam("pageNo") Optional<Integer> pageNo,
            @RequestParam("pageSize") Optional<Integer> pageSize,
            @RequestParam(name = "keyWord", defaultValue = "") String keyWord,
            @RequestParam(name = "sortBy", defaultValue = "") String sortBy,
            @RequestParam(name = "soldOutProduct", defaultValue = "false") Boolean soldOutProduct) {

        // Tách từ khóa thành mảng các chuỗi con
        String[] keywords = keyWord.split(" ");

        // Khởi tạo danh sách để lưu các chuỗi hợp lệ
        List<String> listSearch = new ArrayList<>();

        // Kiểm tra từng từ khóa trong mảng keywords
        Arrays.stream(keywords).forEach(string -> {
            Boolean checkCate = categoryRepository.checkEmptyCategory("%" + string.split(" ") + "%");
            Boolean checkTradeMark = tradeMarkRepository.checkEmptyTradeMark("%" + string.split(" ") + "%");

            // Nếu tồn tại trong category hoặc trademark, thêm từ khóa vào listSearch
            if (checkCate || checkTradeMark) {
                listSearch.add(string);
            }
        });

        // In ra danh sách các từ khóa hợp lệ để kiểm tra
        // System.out.println("Các từ khóa hợp lệ: " + listSearch);

        // Xác định Sort dựa trên sortBy
        Sort sort = Sort.by(Direction.DESC, "p.id"); // Sử dụng tên trường đúng

        switch (sortBy) {
            case "newItems":
                sort = Sort.by(Direction.DESC, "p.id");
                break;
            case "oldItems":
                sort = Sort.by(Direction.ASC, "p.id");
                break;
            case "priceASC":
                sort = Sort.by(Direction.ASC, "maxPrice"); // Sắp xếp theo giá cao nhất tăng dần
                break;
            case "priceDESC":
                sort = Sort.by(Direction.DESC, "maxPrice"); // Sắp xếp theo giá cao nhất giảm dần
                break;
            case "bestSeller":
                sort = Sort.by(Direction.DESC, "orderCount"); // Sắp xếp theo sản phẩm bán chạy
                break;
            case "quantityASC":
                sort = Sort.by(Direction.ASC, "quantityCount");
                break;
            case "quantityDESC":
                sort = Sort.by(Direction.DESC, "quantityCount");
                break;
            default:
                sort = Sort.by(Direction.DESC, "p.id");
                break;
        }

        Pageable pageable = PageRequest.of(pageNo.orElse(0), pageSize.orElse(20), sort);
        Page<Object[]> prPage;
        Integer idCate = null;

        // Kiểm tra keyWord
        if (keyWord.toLowerCase().matches(".*\\bhot\\b.*") || keyWord.toLowerCase().contains("yêu thích")
                || keyWord.toLowerCase().contains("bán chạy") ||
                keyWord.toLowerCase().contains("phổ biến") || keyWord.toLowerCase().contains("được ưa chuộng")
                || keyWord.toLowerCase().contains("hàng đầu")
                || keyWord.toLowerCase().contains("nổi bật")
                || keyWord.toLowerCase().contains("xu hướng") ||
                keyWord.toLowerCase().contains("top")
                || keyWord.toLowerCase().contains("săn đón") ||
                keyWord.toLowerCase().contains("được quan tâm")
                || keyWord.toLowerCase().contains("bán nhiều") ||
                keyWord.toLowerCase().contains("best seller") ||
                keyWord.toLowerCase().contains("bestseller") ||
                keyWord.toLowerCase().contains("best-seller") ||
                keyWord.toLowerCase().contains("được đánh giá cao") ||
                keyWord.toLowerCase().contains("được mua nhiều")) {
            sort = Sort.by(Direction.DESC, "orderCount");

            pageable = PageRequest.of(pageNo.orElse(0), pageSize.orElse(20),
                    sort);

            prPage = productRepository.findAllByStoreIdWithSlugStore(slug,
                    "%",
                    "%",
                    "%",
                    "%",
                    "%",
                    idCate,
                    soldOutProduct,
                    pageable);
        } else {
            pageable = PageRequest.of(pageNo.orElse(0), pageSize.orElse(20),
                    sort);

            // Kiểm tra nếu keyword có thể chuyển đổi thành số
            if (!keyWord.isEmpty()) {
                try {
                    // Tìm theo category ID danh mục
                    idCate = Integer.parseInt(keyWord);
                    prPage = productRepository.findAllByStoreIdWithSlugStore(slug, "", "", "", "", "", idCate,
                            soldOutProduct, pageable);

                } catch (NumberFormatException e) {
                    // Nếu keyword không phải là số thì tìm theo tên hoặc danh mục
                    // Đảm bảo listSearch có đủ 5 phần tử
                    while (listSearch.size() < 5) {
                        listSearch.add(""); // Thêm phần tử rỗng nếu kích thước nhỏ hơn 5
                    }

                    // Tạo một mảng để lưu các tham số cho hàm tìm kiếm
                    String[] searchParams = new String[5];

                    for (int i = 0; i < searchParams.length; i++) {
                        // Kiểm tra xem phần tử có phải là chuỗi không rỗng hay không
                        if (i < listSearch.size() && !listSearch.get(i).isEmpty()) {
                            searchParams[i] = "%" + listSearch.get(i) + "%"; // Thêm ký tự '%' nếu không rỗng
                        } else {
                            searchParams[i] = ""; // Nếu phần tử rỗng, thêm chuỗi rỗng
                        }
                    }

                    // Gọi hàm tìm kiếm với các tham số đã xử lý
                    prPage = productRepository.findAllByStoreIdWithSlugStore(slug,
                            searchParams[0],
                            searchParams[1],
                            searchParams[2],
                            searchParams[3],
                            searchParams[4],
                            null,
                            soldOutProduct,
                            pageable);

                }
            } else {
                prPage = productRepository.findAllByStoreIdWithSlugStore(slug,
                        "%",
                        "%",
                        "%",
                        "%",
                        "%",
                        null,
                        soldOutProduct,
                        pageable);
            }

        }

        // Tạo danh sách sản phẩm từ dữ liệu trả về
        List<Product> products = prPage.getContent().stream()
                .map(obj -> (Product) obj[0]) // Chỉ lấy sản phẩm từ mảng
                .collect(Collectors.toList());

        // Truy vấn từng id
        List<ProductDTO> productDTOs = products.stream().map(product -> {
            List<ProductDetail> productDetails = productDetailRepository.findByIdProduct(product.getId());
            Integer countOrderSuccess = productDetails.stream()
                    .mapToInt((detailProduct) -> orderRepository.countOrderBuyed(detailProduct.getId())).sum();
            return new ProductDTO(product, productDetails, countOrderSuccess);
        }).collect(Collectors.toList());

        // Tạo Map chỉ chứa số liệu cần thiết
        Map<String, Object> response = new HashMap<>();
        response.put("products", productDTOs); // Dánh sách sản phẩm
        response.put("currentPage", prPage.getNumber() + 1); // Trang hiện tại
        response.put("totalPage", prPage.getTotalPages()); // Tổng số trang
        response.put("totalItems", prPage.getTotalElements()); // Tổng số phần tử

        return ResponseEntity.ok(response);
    }

    // GetAllByIdStore
    @GetMapping("/countBySlugProduct/{id}")
    public ResponseEntity<List<Product>> getAllProductByIdStore(@PathVariable("id") Integer id) {
        List<Product> products = productRepository.CountProductByIdStore(id);
        if (products == null || products.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Log để kiểm tra dữ liệu
        products.forEach(product -> {
            System.out.println("Product ID: " + product.getId());
            product.getImages().forEach(image -> System.out
                    .println("Image ID: " + image.getId() + ", Image Name: " + image.getImagename()));
        });
        return ResponseEntity.ok(products);
    }

    @GetMapping("/product/{slug}")
    public ResponseEntity<Product> getBySlugNameProduct(@PathVariable("slug") String slug) {
        return productRepository.findBySlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/productCreate")
    public ResponseEntity<?> createProduct(
            @RequestPart("product") String productJson,
            @RequestPart("productDetails") String productDetailsJson,
            @RequestPart("files") MultipartFile[] files) throws JsonMappingException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Product product;
        List<ProductDetail> productDetails;

        // Chuyển đổi JSON thành đối tượng Product
        product = objectMapper.readValue(productJson, Product.class);

        // Gán tên sản phẩm cho slug
        product.setSlug(slugText.generateUniqueSlug(product.getName()));

        // Lưu Product trước và lấy productId
        Product savedProduct = productRepository.save(product);
        try {
            // Chuyển đổi JSON thành danh sách ProductDetail
            TypeReference<List<ProductDetail>> typeRef = new TypeReference<List<ProductDetail>>() {
            };
            productDetails = objectMapper.readValue(productDetailsJson, typeRef);

            // Duyệt qua từng ProductDetail để lưu vào cơ sở dữ liệu trước để lấy id
            for (ProductDetail detail : productDetails) {
                detail.setProduct(savedProduct); // Gán Product đã lưu vào ProductDetail
                ProductDetail savedDetail = productDetailRepository.save(detail); // Lưu tạm thời ProductDetail để lấy
                                                                                  // id

                try {
                    if (detail.getImagedetail() != null && !detail.getImagedetail().isEmpty()) {
                        // Chuyển đổi chuỗi base64 thành MultipartFile
                        MultipartFile imageDetail = uploadImages.base64ToMultipartFile(detail.getImagedetail());
                        // Lưu hình ảnh lên server và lấy đường dẫn lưu
                        String imageDetailPath = uploadImages.saveDetailProductImage(imageDetail, savedDetail.getId());
                        savedDetail.setImagedetail(imageDetailPath); // Cập nhật đường dẫn thực tế cho imagedetail
                    } else {
                        savedDetail.setImagedetail(null); // Cập nhật đường dẫn thực tế cho imagedetail

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    // Xử lý lỗi khi chuyển đổi base64 thành MultipartFile
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to process image for ProductDetail: " + e.getMessage());
                }

                // Lưu lại ProductDetail sau khi đã cập nhật imagedetail
                productDetailRepository.save(savedDetail);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid data: " + e.getMessage());
        }

        System.out.println("Saved product and details: " + product);

        // Lưu các ảnh trong files vào server và liên kết với Product
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            System.out.println("Received file: " + file.getOriginalFilename());

            // Lưu file và lấy URL
            String imageUrl = fileManagerService.save(file, savedProduct.getId());

            if (imageUrl != null) {
                imageUrls.add(imageUrl);
            }
        }

        // Tạo các đối tượng Image và liên kết với Product
        List<Image> images = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            Image image = new Image();
            image.setImagename(imageUrl);
            image.setProduct(savedProduct);
            images.add(image);
        }

        // Lưu danh sách Image vào cơ sở dữ liệu
        try {
            imageRepository.saveAll(images);
            savedProduct.setImages(images);
            productRepository.save(savedProduct);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update product with images: " + e.getMessage());
        }

        return ResponseEntity.ok("Product created successfully with images and details");
    }

    // Put Store Product
    @PutMapping("/productUpdate/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable("id") Integer id,
            @RequestPart("product") String productJson,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {

        // Kiểm tra xem sản phẩm có tồn tại không
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Chuyển đổi chuỗi JSON thành đối tượng Product
        ObjectMapper objectMapper = new ObjectMapper();
        Product product;
        try {
            product = objectMapper.readValue(productJson, Product.class);
            if (!product.getSlug().isEmpty() || product.getSlug() != null) {
                product.setSlug(slugText.generateUniqueSlug(product.getName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Dữ liệu sản phẩm không hợp lệ: " + e.getMessage());
        }

        // Lưu sản phẩm đã cập nhật
        Product updatedProduct;
        try {
            updatedProduct = productRepository.save(product);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Không thể cập nhật sản phẩm: " + e.getMessage());
        }

        // Xử lý các tệp mới nếu có
        if (files != null && files.length > 0) {
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                System.out.println("Đã nhận tệp: " + file.getOriginalFilename());

                // Lưu tệp và lấy URL hoặc tên tệp
                String imageUrl = fileManagerService.save(file, updatedProduct.getId());

                if (imageUrl != null) {
                    imageUrls.add(imageUrl);
                }
            }

            // Tạo các đối tượng Image cho từng URL hình ảnh mới và liên kết với sản phẩm
            List<Image> images = new ArrayList<>();
            for (String imageUrl : imageUrls) {
                Image image = new Image();
                image.setImagename(imageUrl);
                image.setProduct(updatedProduct);
                images.add(image);
            }

            // Lưu các hình ảnh mới và cập nhật sản phẩm với các đối tượng hình ảnh
            try {
                imageRepository.saveAll(images); // Đảm bảo bạn có repository để lưu hình ảnh
                updatedProduct.getImages().addAll(images); // Thêm hình ảnh mới vào những hình ảnh hiện có
                productRepository.save(updatedProduct); // Lưu sản phẩm đã cập nhật
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Không thể cập nhật sản phẩm với hình ảnh mới: " + e.getMessage());
            }
        }

        return ResponseEntity.ok().build();
    }

    // Delete
    @DeleteMapping("/ProductDelete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        // Kiểm tra xem sản phẩm có tồn tại không
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Lấy thông tin sản phẩm để lấy danh sách hình ảnh
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            // Xóa hình ảnh từ cơ sở dữ liệu
            List<Image> images = product.getImages();
            for (Image image : images) {
                // Xóa hình ảnh khỏi hệ thống tệp
                try {
                    uploadImages.deleteFolderAndFile(
                            Paths.get("src/main/resources/static/files/product-images/" + id).toString());
                } catch (Exception e) {
                    // Xử lý lỗi nếu không thể xóa hình ảnh
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
                imageRepository.delete(image); // Xóa hình ảnh khỏi cơ sở dữ liệu
            }

            // Xóa detailProduct từ cơ sở dữ liệu
            List<ProductDetail> productDetails = productDetailRepository.findByIdProduct(id);
            for (ProductDetail detail : productDetails) {
                // Xóa hình ảnh khỏi hệ thống tệp
                try {
                    uploadImages.deleteFolderAndFile(
                            Paths.get("src/main/resources/static/files/detailProduct/" + detail.getId()).toString());
                } catch (Exception e) {
                    // Xử lý lỗi nếu không thể xóa hình ảnh
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
                productDetailRepository.delete(detail); // Xóa chi tiết sản phẩm khỏi cơ sở dữ liệu
            }
        }

        // Xóa sản phẩm khỏi cơ sở dữ liệu
        productRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }

    // Tìm idStore
    @GetMapping("/searchStore/{id}")
    public ResponseEntity<Store> getIdStoreByIdUser(@PathVariable("id") Integer idUser) {
        Store store = storeRepository.findStoreByIdUser(idUser);
        // if (store == null) {
        // return ResponseEntity.notFound().build();
        // }
        return ResponseEntity.ok(store);
    }

    // CountOrderBuy
    @GetMapping("/countOrderSuccess/{id}")
    public ResponseEntity<Integer> countOrderBuyed(@PathVariable("id") Integer idProductDetail) {
        Integer countOrder = orderRepository.countOrderBuyed(idProductDetail);
        return ResponseEntity.ok(countOrder);
    }

    // Get Top 10 Products by Store Id seller
    @GetMapping("/top10-products/{storeId}")
    public ResponseEntity<List<Map<String, Object>>> getTopSellingProductsByStoreId(@PathVariable Integer storeId) {
        try {
            // Lấy danh sách kết quả từ Repository
            List<Object[]> topSellingProducts = productRepository.findTopSellingProductsByStoreId(storeId);

            // Chuyển đổi danh sách Object[] thành List<Map<String, Object>> cho dễ xử lý
            // trong JSON
            List<Map<String, Object>> products = new ArrayList<>();
            for (Object[] row : topSellingProducts) {
                Map<String, Object> product = new HashMap<>();
                product.put("productId", row[0]); // productId từ câu truy vấn
                product.put("productDetailId", row[1]); // productDetailId từ câu truy vấn
                product.put("productName", row[2]); // productName từ Products table
                product.put("nameDetail", row[3]); // name từ ProductDetails table
                product.put("priceDetail", row[4]); // priceDetail từ ProductDetails table
                product.put("sold", row[5]); // sold (tổng số lượng bán)
                product.put("imageNameDetail", row[6]); // imageNameDetail từ ProductDetails hoặc fallback từ Images
                product.put("productImage", row[7]); // productImage từ Images
                product.put("slugProduct", row[8]); // productImage từ Images

                products.add(product);
            }

            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Phương thức mới để lấy doanh thu theo storeId biểu đồ
    @GetMapping("/revenue/{storeId}")
    public ResponseEntity<List<Map<String, Object>>> getRevenueByStoreId(@PathVariable Integer storeId) {
        List<Object[]> revenueData = productRepository.findRevenueByStoreId(storeId);

        // Chuyển đổi dữ liệu từ Object[] sang Map<String, Object> để có cấu trúc dữ
        // liệu dễ đọc hơn
        List<Map<String, Object>> formattedRevenueData = revenueData.stream().map(record -> {
            Map<String, Object> revenueMap = new HashMap<>();
            revenueMap.put("date", record[0]); // Ngày
            revenueMap.put("revenue", record[1]); // Doanh thu
            return revenueMap;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(formattedRevenueData);
    }

    // biểu đồ tròn
    @GetMapping("/pie-chart/{storeId}")
    public ResponseEntity<List<Map<String, Object>>> getPieChartData(@PathVariable Integer storeId) {
        // Giả sử bạn có một phương thức để lấy danh sách sản phẩm theo storeId
        List<ProductDetail> products = productDetailRepository.findByProduct_StoreId(storeId);

        List<Map<String, Object>> response = new ArrayList<>();
        for (ProductDetail product : products) {
            Map<String, Object> data = new HashMap<>();
            data.put("products", product);
            data.put("value", product.getPrice() * product.getQuantity()); // Tính giá trị
            response.add(data);
        }

        return ResponseEntity.ok(response);
    }

    // biểu đồ mixed-chart
    @GetMapping("/mixed-chart/{storeId}")
    public ResponseEntity<List<Map<String, Object>>> getMixedChartData(@PathVariable Integer storeId) {
        List<Map<String, Object>> data = orderRepository.findRevenueByMonth(storeId);

        // Không cần tính toán thêm, dữ liệu đã được tính trong query
        return ResponseEntity.ok(data);
    }
}
