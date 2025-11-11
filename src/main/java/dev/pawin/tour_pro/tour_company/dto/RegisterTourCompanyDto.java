package dev.pawin.tour_pro.tour_company.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterTourCompanyDto(Integer id, @NotBlank String name, @NotBlank String username, @NotBlank String password, String status) {

}
