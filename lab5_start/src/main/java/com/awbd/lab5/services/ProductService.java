package com.awbd.lab5.services;

import com.awbd.lab5.dtos.ProductDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    List<ProductDTO> findAll();
    ProductDTO findById(Long l);
    ProductDTO save(ProductDTO product);
    void deleteById(Long id);

    public void savePhotoFile(ProductDTO product, MultipartFile file);
}
