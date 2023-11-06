package com.example.ahorcado.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Slf4j
@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(Principal principal) {
        log.info(principal.toString());
        return "home";
    }
}
