package com.example.ahorcado.controller;

import com.example.ahorcado.utilidades.Utilidades;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(HttpServletResponse response) {
        Utilidades.generarCookieFallos(0, response);
        return "home";
    }
}
