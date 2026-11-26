
package com.fintrack.api.fintrack.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.fintrack.api.fintrack.domain.Currency;
import com.fintrack.api.fintrack.domain.Portfolio;

@Repository
public class PortfolioRepository {

    private static final String COLUMNS =
            "id, name, client_name, base_currency, created_at";

    private static final RowMapper<Portfolio> MAPPER = (rs, row) ->
            new Portfolio(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("client_name"),
                    Currency.valueOf(rs.getString("base_currency")),
                    rs.getObject("created_at", OffsetDateTime.class)
            );

    private final JdbcClient jdbc;

    public PortfolioRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<Portfolio> findAllByUser(long userId) {
        return jdbc.sql(
                "SELECT " + COLUMNS +
                " FROM portfolios WHERE user_id = :userId " +
                "ORDER BY created_at, id DESC"
        )
        .param("userId", userId)
        .query(MAPPER)
        .list();
    }

    public Optional<Portfolio> findByIdAndUser(long id, long userId) {
        return jdbc.sql(
                "SELECT " + COLUMNS +
                " FROM portfolios WHERE id = :id AND user_id = :userId"
        )
        .param("id", id)
        .param("userId", userId)
        .query(MAPPER)
        .optional();
    }

    public Optional<Portfolio> lockByIdAndUser(long id, long userId) {
        return jdbc.sql(
                "SELECT " + COLUMNS +
                " FROM portfolios WHERE id = :id AND user_id = :userId FOR UPDATE"
        )
        .param("id", id)
        .param("userId", userId)
        .query(MAPPER)
        .optional();
    }

    public long create(
            long userId,
            String name,
            String clientName,
            Currency currency) {

        KeyHolder keys = new GeneratedKeyHolder();

        jdbc.sql("""
                INSERT INTO portfolios
                (user_id, name, client_name, base_currency)
                VALUES (:userId, :name, :clientName, :currency)
                """)
                .param("userId", userId)
                .param("name", name)
                .param("clientName", clientName)
                .param("currency", currency.name())
                .update(keys, "id");

        return Objects.requireNonNull(keys.getKey()).longValue();
    }

    public boolean update(
            long id,
            long userId,
            String name,
            String clientName,
            Currency currency) {

        return jdbc.sql("""
                UPDATE portfolios
                SET name = :name,
                    client_name = :clientName,
                    base_currency = :currency
                WHERE id = :id AND user_id = :userId
                """)
                .param("name", name)
                .param("clientName", clientName)
                .param("currency", currency.name())
                .param("id", id)
                .param("userId", userId)
                .update() > 0;
    }

    public boolean delete(long id, long userId) {
        return jdbc.sql("""
                DELETE FROM portfolios
                WHERE id = :id AND user_id = :userId
                """)
                .param("id", id)
                .param("userId", userId)
                .update() > 0;
    }
}
