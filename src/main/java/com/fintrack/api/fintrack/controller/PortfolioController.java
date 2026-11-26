
package com.fintrack.api.fintrack.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.fintrack.api.fintrack.domain.Portfolio;
import com.fintrack.api.fintrack.domain.PortfolioSummary;
import com.fintrack.api.fintrack.domain.Transaction;
import com.fintrack.api.fintrack.dto.PortfolioRequest;
import com.fintrack.api.fintrack.dto.TransactionRequest;
import com.fintrack.api.fintrack.security.AuthenticationInterceptor;
import com.fintrack.api.fintrack.service.PortfolioService;
import com.fintrack.api.fintrack.service.TransactionService;

@RestController
@RequestMapping("/api")
public class PortfolioController {

    private static final String USER_ID = AuthenticationInterceptor.USER_ID;

    private final PortfolioService portfolios;
    private final TransactionService transactions;

    public PortfolioController(
            PortfolioService portfolios,
            TransactionService transactions) {
        this.portfolios = portfolios;
        this.transactions = transactions;
    }

    @GetMapping("/portfolios")
    public List<Portfolio> list(
            @RequestAttribute(USER_ID) long userId) {
        return portfolios.list(userId);
    }

    @PostMapping("/portfolios")
    @ResponseStatus(HttpStatus.CREATED)
    public Portfolio create(
            @RequestAttribute(USER_ID) long userId,
            @Valid @RequestBody PortfolioRequest request) {
        return portfolios.create(userId, request);
    }

    @GetMapping("/portfolios/{id}")
    public Portfolio get(
            @RequestAttribute(USER_ID) long userId,
            @PathVariable long id) {
        return portfolios.get(id, userId);
    }

    @PutMapping("/portfolios/{id}")
    public Portfolio update(
            @RequestAttribute(USER_ID) long userId,
            @PathVariable long id,
            @Valid @RequestBody PortfolioRequest request) {
        return portfolios.update(id, userId, request);
    }

    @DeleteMapping("/portfolios/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestAttribute(USER_ID) long userId,
            @PathVariable long id) {
        portfolios.delete(id, userId);
    }

    @GetMapping("/portfolios/{id}/summary")
    public PortfolioSummary summary(
            @RequestAttribute(USER_ID) long userId,
            @PathVariable long id) {
        return portfolios.summary(id, userId);
    }

    @GetMapping("/portfolios/{id}/transactions")
    public List<Transaction> listTransactions(
            @RequestAttribute(USER_ID) long userId,
            @PathVariable long id) {
        return transactions.list(id, userId);
    }

    @PostMapping("/portfolios/{id}/transactions")
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction createTransaction(
            @RequestAttribute(USER_ID) long userId,
            @PathVariable long id,
            @Valid @RequestBody TransactionRequest request) {
        return transactions.create(id, userId, request);
    }

    @DeleteMapping("/transactions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(
            @RequestAttribute(USER_ID) long userId,
            @PathVariable long id) {
        transactions.delete(id, userId);
    }
}
