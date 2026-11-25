package com.fintrack.api.fintrack.repository;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.fintrack.api.fintrack.domain.Users;

import java.util.Objects;
import java.util.Optional;

@Repository
public class UserRepository {
    private final JdbcClient jdbc;
    public UserRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Users> findByEmail(String email) {
        return  jdbc.sql("SELECT id, email, password_hash FROM users WHERE email = :email")
                .param("email", email).
                query((rs, row) ->
        new Users(rs.getLong("id"), rs.getString("email"), rs.getString("password_hash"))).optional();
    }

    public  boolean existById(long id){
        return jdbc.sql("SELECT COUNT(*) FROM users WHERE id = :id")
                .param("id",id)
                .query(Long.class)
                .single() > 0;
    }

    public long create (String email, String passwordHash) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.sql("INSERT INTO users(email, password_hash) VALUES (:email, :passwordHash)")
                .param("email",email)
                .param("password_hash",passwordHash)
                .update(keys, "id");
        return Objects.requireNonNull(keys.getKey()).longValue();
    }
}
