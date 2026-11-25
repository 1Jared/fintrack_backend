package com.fintrack.api.fintrack.domain;

import java.time.OffsetDateTime;

public record Portfolio(long id, String name, String clientName, Currency baseCurrency, OffsetDateTime createdAt) {
}
