package com.fintrack.api.fintrack.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fintrack.api.fintrack.domain.Currency;
import com.fintrack.api.fintrack.domain.Portfolio;
import com.fintrack.api.fintrack.dto.PortfolioRequest;
import com.fintrack.api.fintrack.security.AuthenticationInterceptor;
import com.fintrack.api.fintrack.service.PortfolioService;
import com.fintrack.api.fintrack.service.TransactionService;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class PortfolioControllerTest {

    private PortfolioService portfolios;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        portfolios = mock(PortfolioService.class);
        TransactionService transactions = mock(TransactionService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PortfolioController(portfolios, transactions))
                .build();
    }

    @Test
    void createUsesAuthenticatedUserAttribute() throws Exception {
        when(portfolios.create(eq(42L), any(PortfolioRequest.class)))
                .thenReturn(new Portfolio(
                        7L,
                        "Growth portfolio",
                        "Example client",
                        Currency.KES,
                        OffsetDateTime.parse("2026-11-26T12:00:00Z")));

        mockMvc.perform(post("/api/portfolios")
                        .requestAttr(AuthenticationInterceptor.USER_ID, 42L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Growth portfolio",
                                  "clientName": "Example client",
                                  "baseCurrency": "KES"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.clientName").value("Example client"));
    }
}
