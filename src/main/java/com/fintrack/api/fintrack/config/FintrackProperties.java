
package com.fintrack.api.fintrack.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;
import java.util.List;

@ConfigurationProperties(prefix = "fintrack")
public record FintrackProperties(

        @DefaultValue Jwt jwt,
        @DefaultValue Cors cors,
        @DefaultValue Auth auth,
        @DefaultValue Seed seed

) {

    public record Jwt(
            @DefaultValue("") String secret,
            @DefaultValue("8h") Duration expiration
    ) {}

    public record Cors(
            @DefaultValue("http://localhost:5173")
            List<String> allowedOrigins
    ) {}

    public record Auth(
            @DefaultValue("30") int rateLimit,
            @DefaultValue("15m") Duration rateLimitWindow,
            @DefaultValue("12") int bcryptStrength
    ) {}

    public record Seed(
            @DefaultValue("false") boolean enabled,
            @DefaultValue("") String email,
            @DefaultValue("") String password
    ) {}
}
