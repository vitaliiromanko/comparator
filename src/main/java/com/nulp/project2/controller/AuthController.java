package com.nulp.project2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class AuthController {
    @GetMapping("/login")
    public String showLoginPage(
            @ModelAttribute(value = "messageType") String messageType,
            @ModelAttribute(value = "message") String message,
            Model model
    ) {
        model.addAttribute("selectedPage", "loginPage");
        return "loginPage";
    }
}
