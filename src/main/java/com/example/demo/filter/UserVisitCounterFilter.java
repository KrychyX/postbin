package com.example.demo.filter;

import com.example.demo.service.VisitCounterService;
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
 * Filter that counts visits to the "/users" endpoint.
 * Increments the counter only for HTTP GET requests.
 */
@Component
@Order(1) // Устанавливаем порядок выполнения
public class UserVisitCounterFilter implements Filter {
    private final VisitCounterService visitCounterService;

    /**
     * Constructs the filter with the given visit counter service.
     */
    public UserVisitCounterFilter(VisitCounterService visitCounterService) {
        this.visitCounterService = visitCounterService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if ("GET".equalsIgnoreCase(httpRequest.getMethod())
                && httpRequest.getRequestURI().equals("/users")) {
            visitCounterService.increment();
        }

        chain.doFilter(request, response);
    }
}