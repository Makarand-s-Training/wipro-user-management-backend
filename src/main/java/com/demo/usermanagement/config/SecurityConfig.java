package com.demo.usermanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the application.
 *
 * <p><strong>INTENTIONALLY VULNERABLE CONFIGURATION FOR DEMO PURPOSES</strong>
 *
 * <p>Known vulnerabilities:
 * <ul>
 *   <li>CSRF protection disabled
 *   <li>All endpoints permit all access (no authentication required)
 *   <li>Hardcoded credentials
 *   <li>Passwords stored in plain text (no encoding)
 *   <li>H2 console accessible without authentication
 *   <li>No session management security
 * </ul>
 *
 * <p><strong>WARNING: DO NOT USE THIS CONFIGURATION IN PRODUCTION!</strong>
 *
 * @author Demo Application
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain.
     *
     * <p>VULNERABILITY: Permits all requests without authentication.
     * <p>VULNERABILITY: CSRF protection disabled.
     * <p>VULNERABILITY: Frame options disabled (allows clickjacking).
     *
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // VULNERABILITY: Disable CSRF protection
            .csrf(csrf -> csrf.disable())
            
            // VULNERABILITY: Disable frame options to allow H2 console
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
            
            // VULNERABILITY: Permit all requests without authentication
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()  // VULNERABILITY: H2 console accessible
                .requestMatchers("/admin/**").permitAll()       // VULNERABILITY: Admin endpoints without auth
                .requestMatchers("/users/**").permitAll()       // VULNERABILITY: User endpoints without auth
                .requestMatchers("/actuator/**").permitAll()    // VULNERABILITY: Actuator without auth
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/api-docs/**").permitAll()
                .anyRequest().permitAll()                       // VULNERABILITY: All other endpoints without auth
            );
        
        return http.build();
    }

    /**
     * Provides an in-memory user details service with hardcoded credentials.
     *
     * <p>VULNERABILITY: Hardcoded credentials in source code.
     * <p>VULNERABILITY: No password encoding - plain text passwords.
     * <p>VULNERABILITY: Credentials committed to version control.
     *
     * <p>Hardcoded users:
     * <pre>
     * Username: admin
     * Password: admin123
     * Role: ADMIN
     *
     * Username: user
     * Password: user123
     * Role: USER
     * </pre>
     *
     * @return UserDetailsService with hardcoded users
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // VULNERABILITY: Hardcoded credentials
        UserDetails admin = User.builder()
                .username("admin")
                .password("{noop}admin123")  // VULNERABILITY: {noop} = no password encoding!
                .roles("ADMIN")
                .build();

        UserDetails user = User.builder()
                .username("user")
                .password("{noop}user123")   // VULNERABILITY: {noop} = no password encoding!
                .roles("USER")
                .build();

        // VULNERABILITY: Default user for demo purposes
        UserDetails demo = User.builder()
                .username("demo")
                .password("{noop}demo")      // VULNERABILITY: Weak password
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user, demo);
    }

    // TODO: Implement proper password encoding with BCryptPasswordEncoder
    // TODO: Move credentials to secure vault or environment variables
    // TODO: Enable CSRF protection
    // TODO: Implement proper role-based access control
    // TODO: Add rate limiting to prevent brute force attacks
    // TODO: Implement session management with proper timeout
    // TODO: Add security headers (X-Frame-Options, X-Content-Type-Options, etc.)
}
