package com.fintrack.api.fintrack.domain;

import java.math.BigDecimal;
import java.util.List;

public record PortfolioSummary(Currency baseCurrency, String valuation, List<Holding> holdings, BigDecimal totalInvested, BigDecimal totalValue) {
}
