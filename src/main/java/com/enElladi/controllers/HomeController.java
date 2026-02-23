package com.enElladi.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller(value = "/home")
public class HomeController {

    @GetMapping
    public String home() {
        return "home";
    }

}
