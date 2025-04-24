package com.example.demo.filter;

import com.example.demo.service.CounterService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Filter that counts the number of accesses to article-related endpoints.
 */
@Component
@Order(1)
public class ArticleCounterFilter implements Filter {
    private final CounterService counterService;

    /**
     * Constructs the filter with a given counter service.
     */
    public ArticleCounterFilter(CounterService counterService) {
        this.counterService = counterService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();

        if (path.startsWith("/articles")) {
            counterService.increment(path);
        }

        chain.doFilter(request, response);
    }
}