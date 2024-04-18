package com.awbd.lab7.controllers;

import com.awbd.lab7.dtos.CategoryDTO;
import com.awbd.lab7.services.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @RequestMapping("")
    public String categoryList(Model model) {
        List<CategoryDTO> categories = categoryService.findAll();
        model.addAttribute("categories",categories);
        return "categoryList";
    }

}
