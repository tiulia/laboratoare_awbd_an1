package com.awbd.lab7.services;

import com.awbd.lab7.dtos.CategoryDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {
    List<CategoryDTO> findAll();
    CategoryDTO findById(Long l);
    CategoryDTO save(CategoryDTO category);
    void deleteById(Long id);

}
