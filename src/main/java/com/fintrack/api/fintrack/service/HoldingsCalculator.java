package com.fintrack.api.fintrack.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


import com.fintrack.api.fintrack.domain.Holding;
import com.fintrack.api.fintrack.domain.Transaction;
import com.fintrack.api.fintrack.domain.TransactionType;
import com.fintrack.api.fintrack.error.ApiException;
public final class HoldingsCalculator {
    private static final MathContext PRECISION = MathContext.DECIMAL128;

    private HoldingsCalculator(){

    }

    public  static List<Holding> calculate(List<Transaction> transactions){
        Map<String, Position> positions = new TreeMap<>();
        transactions.stream()
        .sorted(Comparator.comparing(Transaction::tradeDate).thenComparingLong(Transaction::id))
        .forEach(tx -> apply(positions.computeIfAbsent(tx.instrument(), key -> new Position()) , tx));

        List<Holding> holdings  = new ArrayList<>();
        positions.forEach((instrument, position) -> {
            if(position.quantity.signum() > 0){
                  BigDecimal cost =money(position.cost);
                  holdings.add (new Holding(
                    instrument,
                    position.quantity.stripTrailingZeros(),
                    position.cost.divide(position.quantity, PRECISION)
                    .setScale(4,RoundingMode.HALF_UP).stripTrailingZeros(),
                    cost,cost
                  ));
            }
        }
            
        );
        return holdings;
    }

    public static BigDecimal money(BigDecimal value){
        return  value.setScale(2,RoundingMode.HALF_UP).stripTrailingZeros();
    }

    public static  void apply(Position position, Transaction tx){
        if(tx.type() == TransactionType.BUY){
            position.quantity = position.quantity.add(tx.quantity());
            position.cost=position.cost.add(tx.quantity().multiply(tx.unitPrice()));
        }

        int comparison = tx.quantity().compareTo(position.quantity);
        if(comparison > 0){
            throw ApiException.badRequest("Insufficient"+tx.instrument()+" holdings on "+ tx.tradeDate());
        }
        if(comparison==0){
            position.quantity = BigDecimal.ZERO;
            position.cost= BigDecimal.ZERO;
            return ;
        }

        BigDecimal soldCost = position.cost.multiply(tx.quantity().divide(position.quantity,PRECISION));
        position.cost = position.cost.subtract(soldCost);
        position.quantity = position.quantity.subtract(tx.quantity());
    }
    

    private  static  final class Position {
        private BigDecimal quantity = BigDecimal.ZERO;
         private BigDecimal cost = BigDecimal.ZERO;
    }
}
