package com.fintrack.api.fintrack.security;

import org.springframework.web.servlet.HandlerInterceptor;

public class AuthenticationInterceptor implements HandlerInterceptor {
    public static final String USER_ID = "fintrack.userId";
    private static final String BEARER_PREFIX = "bearer ";

    private final TokenService tokenService;
}
