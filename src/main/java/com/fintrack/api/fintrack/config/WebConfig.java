package com.fintrack.api.fintrack.config;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.fintrack.api.fintrack.security.AuthenticationInterceptor;

public class WebConfig implements WebMvcConfigurer {

    private final AuthenticationInterceptor authenticationInterceptor;
    private final FintrackProperties fintrackProperties;

    public WebConfig(AuthenticationInterceptor authenticationInterceptor, FintrackProperties fintrackProperties) {
        this.authenticationInterceptor = authenticationInterceptor;
        this.fintrackProperties = fintrackProperties;
    }
}
