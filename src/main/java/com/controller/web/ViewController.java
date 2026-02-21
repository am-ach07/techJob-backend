package com.controller.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.DTOs.PaginationAndSortDTO;
import com.DTOs.UpdateProfileRequest;
import com.DTOs.notifications.NotificationsDTO;
import com.response.ProfileResponse;
import com.security.jwt.CookieService;
import com.service.NotificationsServiceImp;
import com.service.OrderService;
import com.service.UserSettingService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ViewController {

	
	private final UserSettingService userSettingService;
	private NotificationsServiceImp notificationsServiceImp;
	private final CookieService cookieService;
	private final OrderService orderService;
	
	public ViewController(UserSettingService userSettingService,OrderService orderService,CookieService cookieService,NotificationsServiceImp notificationsServiceImp) {
		super();
		this.userSettingService = userSettingService;
		this.notificationsServiceImp=notificationsServiceImp;
		this.cookieService=cookieService;
		this.orderService=orderService;
	}
	@GetMapping("/")
	public String home () {
		return "dashboard";
	}
	@GetMapping("/profile")
	public String profile (
			Model model) {
		ProfileResponse profile=userSettingService.getMyProfile();

		model.addAttribute("profile",profile);
		return "profile";
	}
	@PostMapping("/profile")
	public String updateProfile(
			BindingResult bindingResult,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model,
			UpdateProfileRequest updateRequest) {
		if (bindingResult.hasErrors()) {
			String csrfToken = cookieService.extractCsrfToken(request);
			model.addAttribute("csrfToken", csrfToken);
			  return "profile";
		}
		try {
			ProfileResponse profile=userSettingService.updateMyProfile(updateRequest);
		model.addAttribute("updateDTO",profile) ;
		return "redirect:/profile?save";
		} catch (Exception e) {
			String csrfToken = cookieService.extractCsrfToken(request);
			model.addAttribute("csrfToken", csrfToken);
			model.addAttribute("error", "حدث خطأ غير متوقع");
		}
		return "profile";
	}
	@GetMapping("/settings")
	public String  settings() {
		return "settings";
	}
	@GetMapping("/notification")
	public String  notification(
			Model model,
			HttpServletResponse response,
            HttpServletRequest request) {
		String csrfToken = cookieService.extractCsrfToken(request);

	    if (csrfToken == null || csrfToken.isEmpty()) {
	        csrfToken = cookieService.generateCsrfToken(response); // ⚠️ ترجع التوكن
	    }

	    model.addAttribute("csrfToken", csrfToken);
		List<NotificationsDTO> notification=notificationsServiceImp.getMyNotifications();
		model.addAttribute("notification", notification);
		
		return "notification";
	}
	@PostMapping("/notification/delete/{id}")
	public String deleteNotification(@PathVariable Long id,
			Model model,
			HttpServletRequest request) {
		String csrfToken = cookieService.extractCsrfToken(request);
		model.addAttribute("csrfToken", csrfToken);
		notificationsServiceImp.deleteNotificationsByID(id);
		return "redirect:/notification";
	}
	@PostMapping("/notification/read/{id}")
	public String markNotificationAsRead(@PathVariable Long id,
			Model model,
			HttpServletRequest request) {
		String csrfToken = cookieService.extractCsrfToken(request);
		model.addAttribute("csrfToken", csrfToken);
		notificationsServiceImp.markAsRead(id);
		return "redirect:/notification";
	}
	@PostMapping("/notification/read")
	public String markAllNotificationAsRead(
			Model model,
			HttpServletRequest request) {
		String csrfToken = cookieService.extractCsrfToken(request);
		model.addAttribute("csrfToken", csrfToken);
		notificationsServiceImp.markAllAsRead();
		return "redirect:/notification";
	}
	@GetMapping("/orders")
	public String  order(
			Model model,
			PaginationAndSortDTO dto) {
		model.addAttribute("order",orderService.clientOrders(dto));
		return "orders";
	}
	
	@GetMapping("/offers")
	public String  offers() {
		return "offers";
	}
	
}
