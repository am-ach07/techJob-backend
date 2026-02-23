package com.config;

import java.time.Duration;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.security.constants.SecurityConstants;

//import io.github.bucket4j.Bandwidth;
//import io.github.bucket4j.BucketConfiguration;
//import io.github.bucket4j.Refill;
//import io.github.bucket4j.distributed.BucketProxy;
//import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.http.HttpServletRequest;

//@Configuration
//public class RateLimitConfig {
//
//    private final ProxyManager<String> proxyManager;
//
//    public RateLimitConfig(ProxyManager<String> proxyManager) {
//        this.proxyManager = proxyManager;
//    }
//
//    @Bean
//    public Function<HttpServletRequest, BucketProxy> rateLimitResolver() {
//        return request -> {
//
//        	String ip = request.getRemoteAddr();
//        	String endpoint = request.getRequestURI()
//        	        .replaceAll("[^a-zA-Z0-9]", "_");
//
//        	String key = "rate_limit:" + endpoint + ":" + ip;
//        	System.out.println("keys"+key);
//            Bandwidth limit;
//
//            if (request.getRequestURI().startsWith(SecurityConstants.AUTH_API_PATH)) {
//                limit = Bandwidth.classic(5,
//                        Refill.intervally(5, Duration.ofMinutes(1)));
//            } else {
//                limit = Bandwidth.classic(20,
//                        Refill.intervally(20, Duration.ofMinutes(1)));
//            }
//
//            BucketConfiguration configuration =
//                    BucketConfiguration.builder()
//                            .addLimit(limit)
//                            .build();
//
//            return proxyManager.builder()
//                    .build(key, configuration);
//        };
//    }
//}
