package com.awbd.lab6.controller;

import com.awbd.lab6.controllers.ProductController;
import com.awbd.lab6.domain.Product;
import com.awbd.lab6.dtos.ProductDTO;
import com.awbd.lab6.services.CategoryService;
import com.awbd.lab6.services.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceControllerTest {

    @Mock
    Model model;
    @Mock
    ProductService productService;

    @Mock
    CategoryService categoryService;
    @InjectMocks
    ProductController productController;
    @Mock
    ModelMapper modelMapper;
    @Test
    public void showById() {
        Long id = 1l;
        Product productTest = new Product();
        productTest.setId(id);

        ProductDTO productTestDTO = new ProductDTO();
        productTestDTO.setId(id);

        //when(modelMapper.map(productTest, ProductDTO.class)).thenReturn(productTestDTO);
        when(productService.findById(id)).thenReturn(productTestDTO);

        String viewName = productController.edit(id.toString(), model);
        assertEquals("productForm", viewName);
        verify(productService, times(1)).findById(id);

        ArgumentCaptor<ProductDTO> argumentCaptor = ArgumentCaptor.forClass(ProductDTO.class);
        verify(model, times(1))
                .addAttribute(eq("product"), argumentCaptor.capture() );

        ProductDTO productArg = argumentCaptor.getValue();
        assertEquals(productArg.getId(), productTestDTO.getId() );

    }

}
