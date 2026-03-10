package com.techJob.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techJob.DTOs.auth.RegisterRequestDTO;
import com.techJob.exception.auth.EmailAlreadyExistsException;
import com.techJob.exception.auth.UsernameAlreadyExistsException;
import com.techJob.security.jwt.CookieService;
import com.techJob.service.AuthenticationService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

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


        model.addAttribute("registerForm", new RegisterRequestDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @ModelAttribute("registerForm") @Valid RegisterRequestDTO dto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        authService.register(dto);
        return "redirect:/auth/login?registered";
    }
}