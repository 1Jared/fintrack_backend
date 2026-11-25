package com.fintrack.api.fintrack.security;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fintrack.api.fintrack.config.FintrackProperties;

import java.time.Duration;

@Service
public class TokenService {

    static final String INVALID_SESSION="Invalid OR expired Session";

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final Duration expiration;

    @Autowired
    public  TokenService(FintrackProperties properties){
        this(properties.jwt().secret(), expiration, verifier, algorithm,properties.jwt().expiration());
    }
}
