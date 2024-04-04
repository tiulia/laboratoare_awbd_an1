package com.awbd.lab4.controllers;

import com.awbd.lab4.domain.Product;
import com.awbd.lab4.dtos.CategoryDTO;
import com.awbd.lab4.dtos.ProductDTO;
import com.awbd.lab4.services.CategoryService;
import com.awbd.lab4.services.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {
    ProductService productService;

    CategoryService categoryService;
    public ProductController(ProductService productService,  CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @RequestMapping("")
    public String productList(Model model) {
        List<ProductDTO> products = productService.findAll();
        model.addAttribute("products",products);
        return "productList";
    }

    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable String id, Model model) {
        model.addAttribute("product",
                productService.findById(Long.valueOf(id)));

        List<CategoryDTO> categoriesAll = categoryService.findAll();
        model.addAttribute("categoriesAll", categoriesAll );

        return "productForm";
    }


    @PostMapping("")
    public String saveOrUpdate(@ModelAttribute ProductDTO product,
                               @RequestParam("imagefile") MultipartFile file
    ){
        if (file.isEmpty())
            productService.save(product);
        else
            productService.savePhotoFile(product, file);


        return "redirect:/product" ;
    }

    @RequestMapping("/delete/{id}")
    public String deleteById(@PathVariable String id){
        productService.deleteById(Long.valueOf(id));
        return "redirect:/product";
    }


    @RequestMapping("/form")
    public String productForm(Model model) {
        Product product = new Product();
        product.setSeller(null);
        model.addAttribute("product",  product);
        List<CategoryDTO> categoriesAll = categoryService.findAll();
        model.addAttribute("categoriesAll", categoriesAll );
        return "productForm";
    }

    @GetMapping("/getimage/{id}")
    public void downloadImage(@PathVariable String id, HttpServletResponse response) throws IOException {
        ProductDTO productDTO = productService.findById(Long.valueOf(id));

        if (productDTO.getInfo() != null) {


            if (productDTO.getInfo().getPhoto() != null) {
                byte[] byteArray = new byte[productDTO.getInfo().getPhoto().length];
                int i = 0;
                for (Byte wrappedByte : productDTO.getInfo().getPhoto()) {
                    byteArray[i++] = wrappedByte;
                }
                response.setContentType("image/jpeg");
                InputStream is = new ByteArrayInputStream(byteArray);
                try {
                    IOUtils.copy(is, response.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
