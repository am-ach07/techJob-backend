package com.techJob.controller.web;

import com.techJob.DTOs.auth.LoginRequestDTO;
import com.techJob.DTOs.auth.RegisterRequestDTO;
import com.techJob.exception.auth.EmailAlreadyExistsException;
import com.techJob.exception.auth.UsernameAlreadyExistsException;
import com.techJob.response.JWTResponseDTO;
import com.techJob.security.jwt.CookieService;
import com.techJobservice.AuthenticationService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.util.Arrays;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AuthController {

	private final AuthenticationService authenticationService;
	private final CookieService cookieService;

	public AuthController(AuthenticationService authenticationService, CookieService cookieService) {
		this.authenticationService = authenticationService;
		this.cookieService = cookieService;
	}

	/* ===================== SIGNUP ===================== */

	@GetMapping("/signup")
	public String signupPage(Model model,
	                         HttpServletResponse response,
	                         HttpServletRequest request) {

	    String csrfToken = cookieService.extractCsrfToken(request);

	    if (csrfToken == null || csrfToken.isEmpty()) {
	        csrfToken = cookieService.generateCsrfToken(response); // ⚠️ ترجع التوكن
	    }

	    model.addAttribute("csrfToken", csrfToken);
	    model.addAttribute("registerRequest", new RegisterRequestDTO());
	    return "auth/signup";
	}


	@PostMapping("/signup")
	public String handleSignup(
			@Valid @ModelAttribute("registerRequest") RegisterRequestDTO registerRequest,
			BindingResult bindingResult,
			Model model,
			HttpServletResponse response,
			HttpServletRequest request) {

		if (bindingResult.hasErrors()) {
			String csrfToken = cookieService.extractCsrfToken(request);
			model.addAttribute("csrfToken", csrfToken);
			  return "auth/signup";
		}

		try {
			authenticationService.register(registerRequest);
			return "redirect:/login?registered";
		} catch (UsernameAlreadyExistsException e) {
			String csrfToken = cookieService.extractCsrfToken(request);
			model.addAttribute("csrfToken", csrfToken);
			model.addAttribute("errorUser", e.getMessage());
			return "auth/signup";
		} catch (EmailAlreadyExistsException e) {
			String csrfToken = cookieService.extractCsrfToken(request);
			model.addAttribute("csrfToken", csrfToken);
			model.addAttribute("errorEmail", e.getMessage());
			return "auth/signup";
		}catch (Exception e) {
			String csrfToken = cookieService.extractCsrfToken(request);
			model.addAttribute("csrfToken", csrfToken);
			model.addAttribute("error", "حدث خطأ غير متوقع");
			return "auth/signup";
		}
	}

	/* ===================== LOGIN ===================== */

	@GetMapping("/login")
	public String loginPage(Model model,
			HttpServletResponse response,
            HttpServletRequest request) {
		String csrfToken = cookieService.extractCsrfToken(request);

	    if (csrfToken == null || csrfToken.isEmpty()) {
	        csrfToken = cookieService.generateCsrfToken(response); // ⚠️ ترجع التوكن
	    }

	    model.addAttribute("csrfToken", csrfToken);
		model.addAttribute("loginRequest", new LoginRequestDTO());
		return "auth/login";
	}

	@PostMapping("/login")
	public String handleLogin(
	        @Valid @ModelAttribute("loginRequest") LoginRequestDTO loginRequest,
	        BindingResult bindingResult,
	        Model model,
	        HttpServletRequest request,
	        HttpServletResponse response) {

	    if (bindingResult.hasErrors()) {

	        String csrfToken = cookieService.extractCsrfToken(request);
	        model.addAttribute("csrfToken", csrfToken);

	        return "auth/login";
	    }

	    try {

	        JWTResponseDTO jwt = authenticationService.login(loginRequest, request);

	        // ✅ هنا الجزء الذي يجب إضافته
	        cookieService.writeAccessToken(
	                response,
	                jwt.getAccessToken(),
	                jwt.getExpiresIn()
	        );

	        cookieService.writeRefreshToken(
	                response,
	                jwt.getRefreshToken(),
	                jwt.getExpiresIn()
	        );

	        return "redirect:/";

	    } catch (Exception ex) {

	        String csrfToken = cookieService.extractCsrfToken(request);
	        model.addAttribute("csrfToken", csrfToken);

	        model.addAttribute("loginError", ex.getMessage());

	        return "auth/login";
	    }
	}

		
	}
	
	
	
	


