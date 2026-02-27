package com.enElladi.controllers;

import com.enElladi.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/home")
public class HomeController {

    private final MessageService messageService;

    @Autowired
    public HomeController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String home(Model model) {
        model.addAttribute("channels", messageService.listOfChannels());
        return "home";
    }

}
