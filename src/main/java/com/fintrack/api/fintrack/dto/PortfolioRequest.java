package com.fintrack.api.fintrack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Currency;

public record PortfolioRequest(
        @NotBlank(message = "Potfolio name is required.")
        @Size(max = 100, message = "Potfolio name must be at most 100 characters.")
        String name,
        @NotBlank(message = "Client name is required.")
        @Size(max = 100, message = "Client name must be at most 100 characters")
        String clientName,
        @NotNull(message = "Currency must be KES or USD.")
        Currency baseCurrency
) {
}
