
package com.fintrack.api.fintrack.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.fintrack.api.fintrack.domain.Transaction;
import com.fintrack.api.fintrack.domain.TransactionType;

@Repository
public class TransactionRepository {

    private static final String COLUMNS =
            "id, portfolio_id, instrument, type, quantity, unit_price, trade_date, created_at";

    private static final RowMapper<Transaction> MAPPER = (rs, row) ->
            new Transaction(
                    rs.getLong("id"),
                    rs.getLong("portfolio_id"),
                    rs.getString("instrument"),
                    TransactionType.valueOf(rs.getString("type")),
                    rs.getBigDecimal("quantity").stripTrailingZeros(),
                    rs.getBigDecimal("unit_price").stripTrailingZeros(),
                    rs.getObject("trade_date", LocalDate.class),
                    rs.getObject("created_at", OffsetDateTime.class)
            );

    private final JdbcClient jdbc;

    public TransactionRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<Transaction> findByPortfolio(long portfolioId) {
        return jdbc.sql("""
                SELECT %s
                FROM transactions
                WHERE portfolio_id = :portfolioId
                ORDER BY trade_date, id
                """.formatted(COLUMNS))
                .param("portfolioId", portfolioId)
                .query(MAPPER)
                .list();
    }

    public Optional<Transaction> findById(long id) {
        return jdbc.sql(
                "SELECT " + COLUMNS +
                " FROM transactions WHERE id = :id"
        )
                .param("id", id)
                .query(MAPPER)
                .optional();
    }

    public Optional<Long> findPortfolioIdOwnedBy(
            long transactionId,
            long userId) {

        return jdbc.sql("""
                SELECT t.portfolio_id
                FROM transactions t
                JOIN portfolios p ON p.id = t.portfolio_id
                WHERE t.id = :id AND p.user_id = :userId
                """)
                .param("id", transactionId)
                .param("userId", userId)
                .query(Long.class)
                .optional();
    }

    public boolean existsForPortfolio(long portfolioId) {
        return jdbc.sql("""
                SELECT EXISTS(
                    SELECT 1
                    FROM transactions
                    WHERE portfolio_id = :portfolioId
                )
                """)
                .param("portfolioId", portfolioId)
                .query(Boolean.class)
                .single();
    }

    public long create(
            long portfolioId,
            String instrument,
            TransactionType type,
            BigDecimal quantity,
            BigDecimal unitPrice,
            LocalDate tradeDate) {

        KeyHolder keys = new GeneratedKeyHolder();

        jdbc.sql("""
                INSERT INTO transactions (
                    portfolio_id,
                    instrument,
                    type,
                    quantity,
                    unit_price,
                    trade_date
                )
                VALUES (
                    :portfolioId,
                    :instrument,
                    :type,
                    :quantity,
                    :unitPrice,
                    :tradeDate
                )
                """)
                .param("portfolioId", portfolioId)
                .param("instrument", instrument)
                .param("type", type.name())
                .param("quantity", quantity)
                .param("unitPrice", unitPrice)
                .param("tradeDate", tradeDate)
                .update(keys, "id");

        return Objects.requireNonNull(keys.getKey()).longValue();
    }

    public void delete(long id) {
        jdbc.sql("""
                DELETE FROM transactions
                WHERE id = :id
                """)
                .param("id", id)
                .update();
    }
}
