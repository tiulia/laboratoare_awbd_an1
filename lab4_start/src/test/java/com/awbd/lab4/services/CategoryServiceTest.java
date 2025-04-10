package com.awbd.lab4.services;

import com.awbd.lab4.domain.Category;
import com.awbd.lab4.dtos.CategoryDTO;
import com.awbd.lab4.mappers.CategoryMapper;
import com.awbd.lab4.repositories.CategoryRepository;
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
        List<Category> categoryList = new ArrayList<>();
        Category category = new Category();
        categoryList.add(category);

        when(categoryRepository.findAll()).thenReturn(categoryList);
        List<CategoryDTO> categoriesDto = categoryService.findAll();
        assertEquals(1, categoriesDto.size());
        verify(categoryRepository, times(1)).findAll();
    }
}
