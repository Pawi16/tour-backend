package dev.pawin.tour_pro.tour_company.model;

import jakarta.validation.constraints.NotBlank;

public record TourCompanyDto (Integer id, @NotBlank String name, String status) {
    
}
