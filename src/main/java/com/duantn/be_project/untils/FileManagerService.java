package com.duantn.be_project.untils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.ServletContext;

@Service
public class FileManagerService {

    private static final String UPLOAD_DIR = "src/main/resources/static/files/product-images/";

    private Path getPath(String folder, String filename) {
        Path dir = Paths.get(UPLOAD_DIR, folder);
        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new RuntimeException("Could not create upload directory!", e);
            }
        }
        return dir.resolve(filename);
    }

    public byte[] read(String folder, String filename) {
        Path path = this.getPath(folder, filename);
        try {
            return Files.readAllBytes(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String save(MultipartFile file, Integer productId) {
        String name = System.currentTimeMillis() + file.getOriginalFilename();
        String filename = Integer.toHexString(name.hashCode()) + name.substring(name.lastIndexOf("."));
        Path path = this.getPath(productId.toString(), filename);
        try {
            file.transferTo(path);
            return filename;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void delete(String folder, String filename) {
        Path path = this.getPath(folder, filename);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> list(String folder) {
        List<String> filenames = new ArrayList<>();
        Path dir = Paths.get(UPLOAD_DIR, folder);
        if (Files.exists(dir)) {
            try {
                Files.list(dir).forEach(file -> {
                    if (Files.isRegularFile(file)) {
                        filenames.add(file.getFileName().toString());
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filenames;
    }
}
