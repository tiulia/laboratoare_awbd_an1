package com.awbd.lab7.services;

import com.awbd.lab7.dtos.ProductDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    List<ProductDTO> findAll();
    ProductDTO findById(Long l);
    ProductDTO save(ProductDTO product);
    void deleteById(Long id);

    public void savePhotoFile(ProductDTO product, MultipartFile file);
}
