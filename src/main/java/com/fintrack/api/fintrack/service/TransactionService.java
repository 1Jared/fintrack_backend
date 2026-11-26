package com.fintrack.api.fintrack.service;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fintrack.api.fintrack.domain.Transaction;
import com.fintrack.api.fintrack.dto.TransactionRequest;
import com.fintrack.api.fintrack.error.ApiException;
import com.fintrack.api.fintrack.repository.PortfolioRepository;
import com.fintrack.api.fintrack.repository.TransactionRepository;

@Service 
public class TransactionService {
    private final PortfolioService portfolioService;

    private static final Pattern INSTRUMENT = Pattern.compile("[A-Z0-9][A-Z0-9.-]*");

    private final PortfolioRepository portfolios;
    private final TransactionRepository transactions;

    public TransactionService(PortfolioRepository portfolios, TransactionRepository transactions, PortfolioService portfolioService){
        this.portfolios = portfolios;
        this.transactions = transactions;
        this.portfolioService = portfolioService;
    }

    @Transactional(readOnly = true)
    public List<Transaction> list(long portfolioId, long userId){
        portfolios.findByIdAndUser(portfolioId, userId).orElseThrow(
            ()-> ApiException.notFound(portfolioService.NOT_FOUND)
            
        );
        return  transactions.findByPortfolio(portfolioId);
    }


    @Transactional 
    public  Transaction create(long portfolioId, long userId, TransactionRequest request){
        portfolios.lockByIdAndUser(portfolioId, userId)
        .orElseThrow(
            ()-> ApiException.notFound(PortfolioService.NOT_FOUND)
        );
        String instrument = request.instrument().trim().toUpperCase(Locale.ROOT);
        if(!INSTRUMENT.matcher(instrument).matches()){
            throw ApiException.badRequest("Instrument must contain letters, numbers, dots or hyphen");
        }

        long id = transactions.create(portfolioId, instrument, request.type(), request.quantity(), request.unitPrice(), request.tradeDate());

        HoldingsCalculator.calculate(transactions.findByPortfolio(portfolioId));
        return transactions.findById(id).orElseThrow();
    }
    

    @Transactional 
    public void delete(long transactionId, long userId){
        long portfolioId = transactions.findPortfolioIdOwnedBy(transactionId, userId)
        .orElseThrow(()-> ApiException.notFound("TRansaction not found"));

        portfolios.lockByIdAndUser(portfolioId, userId)
        .orElseThrow(()-> ApiException.notFound("TRansaction not found"));
        transactions.delete(transactionId);
        HoldingsCalculator.calculate(transactions.findByPortfolio(portfolioId));
    }
}
