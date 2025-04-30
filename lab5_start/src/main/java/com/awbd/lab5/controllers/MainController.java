package com.awbd.lab5.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/")
public class MainController {


    @RequestMapping("")
    public String productForm() {

        return "main";
    }



}