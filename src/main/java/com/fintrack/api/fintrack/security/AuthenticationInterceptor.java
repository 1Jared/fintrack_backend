
package com.fintrack.api.fintrack.security;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fintrack.api.fintrack.error.ApiException;
import com.fintrack.api.fintrack.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    public static final String USER_ID = "fintrack.userId";

    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenService tokenService;
    private final UserRepository users;

    public AuthenticationInterceptor(
            TokenService tokenService,
            UserRepository users
    ) {
        this.tokenService = tokenService;
        this.users = users;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {

        if (CorsUtils.isPreFlightRequest(request)) {
            return true;
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null
                || header.length() <= BEARER_PREFIX.length()
                || !header.regionMatches(
                        true,
                        0,
                        BEARER_PREFIX,
                        0,
                        BEARER_PREFIX.length()
                )) {
            throw ApiException.unauthorized("authentication needed");
        }

        long userId = tokenService.verify(
                header.substring(BEARER_PREFIX.length()).trim()
        );

        if (!users.existById(userId)) {
            throw ApiException.unauthorized(TokenService.INVALID_SESSION);
        }

        request.setAttribute(USER_ID, userId);

        return true;
    }
}
