package com.awbd.lab4.controllers;

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