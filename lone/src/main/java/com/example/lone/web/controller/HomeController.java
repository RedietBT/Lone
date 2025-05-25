package com.example.lone.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index"; // Corresponds to src/main/resources/templates/index.html
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Corresponds to src/main/resources/templates/login.html
    }

    @GetMapping("/register")
    public String register() {
        return "register"; // Corresponds to src/main/resources/templates/register.html
    }
}