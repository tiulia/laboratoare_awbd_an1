package com.awbd.lab6.controller;

import com.awbd.lab6.domain.Info;
import com.awbd.lab6.dtos.ProductDTO;
import com.awbd.lab6.exceptions.ResourceNotFoundException;
import com.awbd.lab6.services.ProductService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.web.servlet.function.RequestPredicates.contentType;

@SpringBootTest
@AutoConfigureMockMvc
@Profile("mysql")
public class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProductService productService;

    @MockBean
    Model model;

    @Test
    @WithMockUser(username = "admin", password = "12345", roles = "ADMIN")
    public void showByIdMvc() throws Exception {
        Long id = 1l;
        ProductDTO productTestDTO = new ProductDTO();
        productTestDTO.setId(id);
        productTestDTO.setName("test");

        when(productService.findById(id)).thenReturn(productTestDTO);

        mockMvc.perform(get("/product/edit/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("productForm"))
                .andExpect(model().attribute("product", productTestDTO));
    }

    @Test
    @WithMockUser(username = "admin", password = "12345", roles = "ADMIN")
    public void showByIdNotFound() throws Exception {
        Long id = 1l;

        when(productService.findById(id)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/product/edit/{id}", "1"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("notFoundException"))
                .andExpect(model().attributeExists("exception"));
    }

    @Test
    @WithMockUser(username = "guest", password = "12345", roles = "GUEST")
    public void showProductForm() throws Exception {

        mockMvc.perform(get("/product/form"))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(username = "admin", password = "12345", roles = "ADMIN")
    public void showProductFormAdmin() throws Exception {

        mockMvc.perform(get("/product/form"))
                .andExpect(status().isOk())
                .andExpect(view().name("productForm"))
                .andExpect(model().attributeExists("product"))
        ;
    }

    @Test
    @WithMockUser(username = "admin", password = "12345", roles = "ADMIN")
    public void getImage() throws Exception {
        Long id = 1l;
        ProductDTO productTestDTO = new ProductDTO();
        productTestDTO.setId(id);
        productTestDTO.setName("test");

        Info info =  new Info();
        byte[] imageBytes = { 0x12, 0x34, 0x56, 0x78};
        info.setPhoto(imageBytes);
        productTestDTO.setInfo(info);

        when(productService.findById(id)).thenReturn(productTestDTO);


        mockMvc.perform(get("/product/getimage/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));

    }

    @Test
    @WithMockUser(username = "admin", password = "12345", roles = "ADMIN")
    public void testSaveOrUpdate_WithValidProductAndNoFile_ShouldSaveProduct() throws Exception {
        ProductDTO product = new ProductDTO();
        product.setName("Test Product");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/product").file("imagefile", new byte[0])
                        .with(csrf())
                        .param("name", "Test Product")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product"));

        ArgumentCaptor<ProductDTO> argumentCaptor = ArgumentCaptor.forClass(ProductDTO.class);
        verify(productService, times(1))
                .save(argumentCaptor.capture() );

        ProductDTO productArg = argumentCaptor.getValue();
        assertEquals(productArg.getName(), product.getName() );
    }


}