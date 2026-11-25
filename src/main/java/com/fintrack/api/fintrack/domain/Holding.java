package com.fintrack.api.fintrack.domain;

import java.math.BigDecimal;

public record Holding(String instrument, BigDecimal netQuantity, BigDecimal averageCost, BigDecimal totalInvested, BigDecimal totalValue) {
}
