package com.security.ratelimit;

import java.io.IOException;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.security.constants.SecurityConstants;
import com.service.AuthAuditLogService;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.distributed.BucketProxy;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Function<HttpServletRequest, BucketProxy> resolver;
    private final AuthAuditLogService auditLogService;
    private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);

    
    
    
    
    public RateLimitFilter(
    		Function<HttpServletRequest, BucketProxy> resolver,
    		AuthAuditLogService auditLogService) {
        this.resolver = resolver;
        this.auditLogService=auditLogService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

    	BucketProxy bucket = resolver.apply(request);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
        	auditLogService.log(
                    null,
                    SecurityConstants.AUTH_API_PATH,
                    request.getRemoteAddr(),
                    request.getHeader(SecurityConstants.USER_AGENT_HEADER),
                    SecurityConstants.FAILED_ACTION
            );
            logger.warn("user with ip {} in device {} tried many request",request.getRemoteAddr(),request.getHeader(SecurityConstants.USER_AGENT_HEADER));
      
            response.setStatus(429);
            response.getWriter().write("Too many requests");
        }
    }
}

