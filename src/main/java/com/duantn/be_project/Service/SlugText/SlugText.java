package com.duantn.be_project.Service.SlugText;

import java.text.Normalizer;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duantn.be_project.Repository.ProductRepository;

@Service
public class SlugText {

    @Autowired 
    ProductRepository productRepository;
    
    // Hàm tạo slug duy nhất
    public String generateUniqueSlug(String name) {
        String baseSlug = convertToSlug(name);
        String uniqueSlug = baseSlug;
        int counter = 1;

        // Kiểm tra slug đã tồn tại hay chưa
        while (productRepository.findBySlug(uniqueSlug).isPresent()) {
            uniqueSlug = baseSlug + "-" + counter; // Thêm hậu tố nếu slug bị trùng
            counter++;
        }

        return uniqueSlug;
    }

    // Chuyển tên thành slug
    private String convertToSlug(String name) {
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
    }
}
