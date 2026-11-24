package com.fintrack.api.fintrack;

import com.fintrack.api.fintrack.domain.Portfolio;
import org.springframework.stereotype.Repository;

import javax.swing.tree.RowMapper;

@Repository
public class PortfolioRepository {
    private  static  final String COLUMNS="id, name, client_name, base_currency, created_at";
    private static final RowMapper<Portfolio> MAPPER= (rs,row)-> new Portfolio(
            rs.getLong
    )
}
