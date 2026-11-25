package com.fintrack.api.fintrack.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fintrack.api.fintrack.config.FintrackProperties;
import com.fintrack.api.fintrack.error.ApiException;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

@Service
public class TokenService {

    static final String INVALID_SESSION="Invalid OR expired Session";

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final Duration expiration;

    @Autowired
    public  TokenService(FintrackProperties properties){
        this(properties.jwt().secret(),properties.jwt().expiration());
    }

    public TokenService(String secret, Duration expiration){
        if(secret == null || secret.length() < 32 ){
            throw new IllegalStateException("JWT_SECRET must be set to at least 32 characters");
        }
        this.algorithm = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));
        this.verifier =JWT.require(algorithm).build();
        this.expiration = expiration;
    }

    public String createToken(long userId, String email){
        Instant now = Instant.now();
        return  JWT.create().withSubject(Long.toString(userId))
        .withClaim("email", email)
        .withIssuedAt(now)
        .withExpiresAt(now.plus(expiration))
        .sign(algorithm);
    }

    public long verify(String token){
        try{
            long userId =Long.parseLong(verifier.verify(token).getSubject());
            if(userId < 1){
                throw ApiException.unauthorized(INVALID_SESSION);
            }
            return  userId;
        }catch(JWTVerificationException | NumberFormatException exception){
            throw ApiException.unauthorized(INVALID_SESSION);
        }
    }
}
