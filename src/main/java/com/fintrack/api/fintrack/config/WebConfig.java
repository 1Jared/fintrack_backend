package com.fintrack.api.fintrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.fintrack.api.fintrack.security.AuthenticationInterceptor;

@Configuration 
public class WebConfig implements WebMvcConfigurer {

    private final AuthenticationInterceptor authenticationInterceptor;
    private final FintrackProperties properties;

    public WebConfig(AuthenticationInterceptor authenticationInterceptor, FintrackProperties fintrackProperties) {
        this.authenticationInterceptor = authenticationInterceptor;
        this.properties = fintrackProperties;
    }

    @Bean 
    PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder(properties.auth().bcryptStrength());
    }

    @Override 
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/api/**")
        .allowedOrigins(properties.cors().allowedOrigins().toArray(String[]::new ))
        .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
        .allowedHeaders("Authorization","Content-Type");
    }

    @Override 
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(authenticationInterceptor)
        .addPathPatterns("/api/**")
        .excludePathPatterns("/api/health","/api/auth/**");
    }


}
