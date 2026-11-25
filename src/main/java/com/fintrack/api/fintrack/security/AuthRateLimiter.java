package com.fintrack.api.fintrack.security;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fintrack.api.fintrack.config.FintrackProperties;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component 
public class AuthRateLimiter extends OncePerRequestFilter{

    private final int limit;
    private final long windowMillis;
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();
    private final AtomicInteger requestCount = new AtomicInteger();

    @Autowired 
    public AuthRateLimiter(FintrackProperties properties){
        this(properties.auth().rateLimit() , properties.auth().rateLimitWindow());
    }

    public AuthRateLimiter(int limit, Duration window){
        this.limit = limit;
        this.windowMillis = window.toMillis();
    }

    @Override 
    protected boolean shouldNotFilter(HttpServletRequest request){
        return "OPTIONS".equalsIgnoreCase(request.getMethod()) || !request.getRequestURI().startsWith("/api/auth/");
    }

    @Override 
    protected  void doFilterInternal(HttpServletRequest request , HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        long now = System.currentTimeMillis();
        Window window = windows.computeIfAbsent(request.getRemoteAddr(), ignored -> new Window(now));
        boolean allowed;
        synchronized(window) {
            if(now - window.startedAt >= windowMillis){
                window.startedAt = now;
                window.requests = 0;
            }
            allowed = window.requests < limit;
            if(allowed){
                window.requests++;
            }
        }
         if((requestCount.incrementAndGet() & 255)== 0){
            windows.entrySet().removeIf(entry -> now - entry.getValue().startedAt >= windowMillis);
        }

        if(!allowed){
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\":{\"message\":\"Too many attempts.\"}}");
            return ;
        }
       chain.doFilter(request, response);
    }

    private static final class Window{
    private long startedAt;
    private  int requests;

    private Window(long startedAt){
        this.startedAt = startedAt;
    }
 }
    
}


