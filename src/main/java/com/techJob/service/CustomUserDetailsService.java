package com.techJob.service;

import java.util.List;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.techJob.domain.entity.User;
import com.techJob.exception.auth.InvalidLoginCredentialsException;
import com.techJob.exception.user.UserNotFoundException;
import com.techJob.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws AuthenticationException {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new 	InvalidLoginCredentialsException("Invalid username/email or password"));

        GrantedAuthority authority = new SimpleGrantedAuthority(
            user.getRole() != null && user.getRole().toString().startsWith("ROLE_")
                ? user.getRole().toString()
                : "ROLE_" + user.getRole().toString()
        );

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(authority)
        );
    }
    /**
     * Load user by publicID for stable authentication (recommended for JWT).
     */
    public UserDetails loadUserByPublicID(String publicID) throws AuthenticationException {
        User user = userRepository.findByPublicID(publicID)
                .orElseThrow(() -> new 	InvalidLoginCredentialsException("User not found with publicID: " + publicID));
        GrantedAuthority authority = new SimpleGrantedAuthority(
            user.getRole() != null && user.getRole().toString().startsWith("ROLE_")
                ? user.getRole().toString()
                : "ROLE_" + user.getRole().toString()
        );
        logger.info("Loaded user by publicID: {}", publicID);
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(authority)
        );
    }
    // Security improvement: UserDetailsService now supports user publicID-based lookups for stable authentication. The 'username' claim in JWTs is for display/audit only and is not used for validation, so username changes do not invalidate JWTs.
}