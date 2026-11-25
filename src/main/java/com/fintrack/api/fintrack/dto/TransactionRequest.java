package com.fintrack.api.fintrack.dto;

import com.fintrack.api.fintrack.domain.TransactionType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(
        @NotBlank(message = "Instrument is required")
        @Size(max = 30, message = "Instrument must be at most 30 characters")
        String instrument,
        @NotNull(message = "Type must be BUY or SELL")
        TransactionType type,
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantitiy must be positive")
        @DecimalMax(value = "1000000000", message = "Quantity must be at most 1,000,000,000.")
        @Digits(integer = 10, fraction = 4, message = "Quantity must have at most 4 decimal places.")
        BigDecimal quantity,
        @NotNull(message = "Unit price is required")
        @Positive(message = "Unit price must be positive")
        @DecimalMax(value = "1000000000", message = "Unit price must be at most 1,000,000,000.")
        @Digits( integer =10, fraction = 4, message = "Umit price must have at most 4 decimal places.")
        BigDecimal unitPrice,
        @NotNull(message = "Trade date is required(YYYY-MM-DD).")
        LocalDate tradeDate
) {
}
