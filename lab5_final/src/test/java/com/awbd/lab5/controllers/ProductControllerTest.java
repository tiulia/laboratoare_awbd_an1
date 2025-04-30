package com.awbd.lab5.controllers;

import com.awbd.lab5.dtos.ProductDTO;
import com.awbd.lab5.services.CategoryService;
import com.awbd.lab5.services.ProductService;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Profile("mysql")
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    ProductService productService;

    @MockitoBean
    CategoryService categoryService;

    @MockitoBean
    Model model;

    @Test
    public void showByIdMvc() throws Exception {
        Long id = 1l;

        ProductDTO productTestDTO = new ProductDTO();
        productTestDTO.setId(id);
        productTestDTO.setName("test");

        when(productService.findById(id)).thenReturn(productTestDTO);

        mockMvc.perform(get("/products/edit/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("productForm"))
                .andExpect(model().attribute("product", productTestDTO));

    }


    @Test
    public void testSaveOrUpdate_WithValidProductAndNoFile_ShouldSaveProduct() throws Exception {
        ProductDTO product = new ProductDTO();
        product.setName("Test Product");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/products").file("imagefile", new byte[0])
                        .param("name", "Test Product")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));

        ArgumentCaptor<ProductDTO> argumentCaptor = ArgumentCaptor.forClass(ProductDTO.class);
        verify(productService, times(1))
                .save(argumentCaptor.capture() );

        ProductDTO productArg = argumentCaptor.getValue();
        assertEquals(productArg.getName(), product.getName() );
    }
}