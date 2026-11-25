package com.fintrack.api.fintrack.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fintrack.api.fintrack.domain.Portfolio;
import com.fintrack.api.fintrack.domain.PortfolioSummary;
import com.fintrack.api.fintrack.dto.PortfolioRequest;
import com.fintrack.api.fintrack.error.ApiException;
import com.fintrack.api.fintrack.repository.PortfolioRepository;
import com.fintrack.api.fintrack.repository.TransactionRepository;

@Service 
public class PortfolioService {
    static final String NOT_FOUND = "Portfolio not found";

    private final PortfolioRepository portfolios;
    private TransactionRepository transactions;

    public  PortfolioService( PortfolioRepository portfolios, TransactionRepository transactions){
        this.transactions = transactions;
        this.portfolios =portfolios;
    }

    public List<Portfolio> list (long userId ){
        return  portfolios.findAllByUser(userId);
    }

    public Portfolio get(long id, long userId){
        return portfolios.findByIdAndUser(id, userId).orElseThrow(()->ApiException.notFound(NOT_FOUND));
    }

    @Transactional 
    public Portfolio create( long userId , PortfolioRequest request){
        long id = portfolios.create(userId, request.name().trim(), request.clientName().trim(), request.baseCurrency());
        return get(id, userId);
    }

    @Transactional 
    public Portfolio update(long id, long userId, PortfolioRequest request){
        Portfolio existing = portfolios.lockByIdAndUser(id, userId).orElseThrow(() -> ApiException.notFound(NOT_FOUND));
        if(existing.baseCurrency() != request.baseCurrency() && transactions.existsForPortfolio(id)){
            throw ApiException.badRequest("Currency cant be changed after transactions exist");
        }
        portfolios.update(id, request.name().trim(), request.clientName().trim(), request.baseCurrency());

        return get(id, userId);
    }

    @Transactional 
    public  void delete(long id, long userId){
        if( !portfolios.delete(id, userId))
        {
            throw ApiException.notFound(NOT_FOUND);
        }
    }

    // @Transactional 
    // public PortfolioSummary summary (long id, long userId){
    //     Portfolio portfolio = get(id, userId);

    //     List <Holding> holdings = HoldingsCalculator.
    // }
    
}
