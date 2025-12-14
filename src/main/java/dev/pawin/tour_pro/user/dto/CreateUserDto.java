package dev.pawin.tour_pro.user.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateUserDto(
        @NotBlank String firstName, @NotBlank String lastName, String phoneNumber, @NotBlank String email, @NotBlank String password
) {
} 