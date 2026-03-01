package com.techJob.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techJob.DTOs.auth.RegisterRequestDTO;
import com.techJob.security.jwt.CookieService;
import com.techJob.service.AuthenticationService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/auth")
public class AuthViewController {

    private final AuthenticationService authService;
    private final CookieService cookieService;

    public AuthViewController(
    		AuthenticationService authService,
    		CookieService cookieService) {
        this.authService = authService;
		this.cookieService = cookieService;
    }

    @GetMapping("/register")
    public String showRegisterPage(HttpServletResponse response, Model model) {

        cookieService.generateCsrfToken(response);

        model.addAttribute("registerForm", new RegisterRequestDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @ModelAttribute RegisterRequestDTO dto,
            BindingResult bindingResult,
            Model model
    ) {
    	
    	if (bindingResult.hasErrors()) {
			return "auth/register";
		}
    	
    	
        try {
            authService.register(dto); // عندك service جاهز

            return "redirect:/auth/login?registered";

        } catch (Exception ex) {

            model.addAttribute("errorMessage", ex.getMessage());
            return "auth/register";
        }
    }
}