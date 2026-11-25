package com.fintrack.api.fintrack.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record Transaction(long id, long portfolioId, String instrument, TransactionType type, BigDecimal quantity, BigDecimal unitPrice, LocalDate tradeDate, OffsetDateTime createdAt) {
}
