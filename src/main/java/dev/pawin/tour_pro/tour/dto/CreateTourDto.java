package dev.pawin.tour_pro.tour.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTourDto(
        @NotNull Integer tourCompanyId,
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String location,
        Integer numberOfPeople,
        @NotNull Instant activityDate,
        String status) {

}
