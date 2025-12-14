package dev.pawin.tour_pro.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserDto(
        @NotBlank String firstName, @NotBlank String lastName
) {
    
}
