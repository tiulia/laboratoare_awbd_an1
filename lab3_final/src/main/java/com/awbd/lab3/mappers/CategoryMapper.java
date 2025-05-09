package com.awbd.lab3.mappers;

import com.awbd.lab3.domain.Category;
import com.awbd.lab3.dtos.CategoryDTO;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryDTO toDto(Category category) {
        Long id = category.getId();
        String name= category.getName();
        return new CategoryDTO(id, name);
    }

    public Category toCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setId(categoryDTO.getId());
        category.setName(categoryDTO.getName());
        return category;
    }
}