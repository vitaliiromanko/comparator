package com.nulp.project2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GreetingController {
    @GetMapping("/")
    public String showGreetingPage(Model model) {
        model.addAttribute("selectedPage", "greetingPage");
        return "greetingPage";
    }
}
