package com.techJob.controller.web;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.techJob.DTOs.PaginationAndSortDTO;
import com.techJob.DTOs.serviceOffer.ServiceOfferDTO;
import com.techJob.service.ServiceOfferService;


@Controller
@RequestMapping("/offers")
public class OfferViewController {

    private final ServiceOfferService serviceOfferService;

    public OfferViewController(ServiceOfferService serviceOfferService) {
        this.serviceOfferService = serviceOfferService;
    }

    @GetMapping
    public String getOffers(
            @RequestParam(required = false) PaginationAndSortDTO dto,
            Model model
    ) {

        Page<ServiceOfferDTO> response =
                serviceOfferService.getPublicOffer(dto);

        model.addAttribute("offersPage", response);
        model.addAttribute("currentPage", dto.getPage());
        model.addAttribute("totalPages", response.getTotalPages());
        model.addAttribute("sort", dto.getSort());

        return "offers/list";
    }
    
    @GetMapping("/{offerId}")
    public String getOfferDetails(@PathVariable String offerId, Model model) {

        ServiceOfferDTO response =
                serviceOfferService.getOfferByPublicID(offerId);

        model.addAttribute("offer", response);

        return "offers/details";
    }
}