package com.demo.usermanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * CORS (Cross-Origin Resource Sharing) configuration.
 *
 * <p>VULNERABILITY: Overly permissive CORS configuration for demo purposes.
 * Allows all origins, methods, and headers which is a security risk.
 *
 * <p><strong>WARNING: DO NOT USE IN PRODUCTION!</strong>
 * Production apps should restrict origins to specific trusted domains.
 *
 * @author Demo Application
 * @version 1.0
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Configure CORS mappings for all endpoints.
     *
     * <p>VULNERABILITY: Allows all origins (*) which is insecure.
     * <p>VULNERABILITY: Allows all HTTP methods.
     * <p>VULNERABILITY: Allows all headers.
     * <p>VULNERABILITY: Exposes all response headers.
     *
     * @param registry the CORS registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")  // VULNERABILITY: Allow all origins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")         // VULNERABILITY: Allow all headers
                .exposedHeaders("*")         // VULNERABILITY: Expose all headers
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * CORS configuration source bean for Spring Security integration.
     *
     * <p>VULNERABILITY: Permissive CORS settings that allow any origin.
     *
     * @return CorsConfigurationSource with permissive settings
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // VULNERABILITY: Allow all origins (should be specific domains in production)
        configuration.setAllowedOriginPatterns(List.of("*"));
        
        // VULNERABILITY: Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));
        
        // VULNERABILITY: Allow all headers
        configuration.setAllowedHeaders(List.of("*"));
        
        // VULNERABILITY: Expose all headers to client
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With", 
            "Accept", "Origin", "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    // TODO: In production, restrict to specific origins:
    // configuration.setAllowedOrigins(Arrays.asList(
    //     "https://yourdomain.com",
    //     "https://app.yourdomain.com"
    // ));
    
    // TODO: Limit methods to only what's needed
    // TODO: Restrict headers to specific ones
    // TODO: Don't expose sensitive headers
    // TODO: Consider shorter maxAge for sensitive applications
}
