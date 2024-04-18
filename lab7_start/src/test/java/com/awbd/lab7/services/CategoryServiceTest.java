package com.awbd.lab7.services;

import com.awbd.lab7.domain.Category;
import com.awbd.lab7.dtos.CategoryDTO;
import com.awbd.lab7.mappers.CategoryMapper;
import com.awbd.lab7.repositories.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    CategoryMapper categoryMapper;
    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryServiceImpl categoryService;

    @Test
    public void findProducts() {
        List<Category> categoryList = new ArrayList<Category>();
        Category category = new Category();
        categoryList.add(category);

        when(categoryRepository.findAll()).thenReturn(categoryList);
        List<CategoryDTO> categriesDto = categoryService.findAll();
        assertEquals(categriesDto.size(), 1);
        verify(categoryRepository, times(1)).findAll();
    }
}
