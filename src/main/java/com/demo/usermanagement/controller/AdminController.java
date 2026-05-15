package com.demo.usermanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Admin controller for administrative operations.
 *
 * <p><strong>VULNERABILITY: No authentication or authorization required!</strong>
 * <p>This controller exposes sensitive administrative data without any security checks.
 *
 * <p>Example API usage:
 * <pre>
 * GET /admin/secrets - Returns sensitive configuration data (NO AUTH REQUIRED!)
 * </pre>
 *
 * @author Demo Application
 * @version 1.0
 */
@RestController
@RequestMapping("/admin")
@CrossOrigin(originPatterns = "*", maxAge = 3600)  // VULNERABILITY: Allow all origins
@Slf4j
@Tag(name = "Admin (VULNERABLE)", description = "Admin endpoints - NO AUTHENTICATION REQUIRED!")
public class AdminController {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    /**
     * Returns sensitive system configuration and secrets.
     *
     * <p><strong>VULNERABILITY: SENSITIVE DATA EXPOSURE</strong>
     * <p>This endpoint exposes sensitive configuration data including:
     * <ul>
     *   <li>Database credentials
     *   <li>Internal system paths
     *   <li>Application secrets
     *   <li>No authentication required to access!
     * </ul>
     *
     * <p>Attack impact:
     * <pre>
     * Anyone can access this endpoint without authentication and obtain:
     * - Database connection strings
     * - Database credentials
     * - System configuration details
     * </pre>
     *
     * @return sensitive configuration data (UNSAFE - no auth required)
     */
    @GetMapping("/secrets")
    @Operation(summary = "Get sensitive secrets (VULNERABLE)", 
               description = "Returns sensitive system configuration - NO AUTHENTICATION REQUIRED!")
    public ResponseEntity<Map<String, Object>> getSecrets() {
        log.warn("SECURITY ALERT: /admin/secrets endpoint accessed without authentication!");
        
        // VULNERABILITY: Exposing sensitive data without authentication
        Map<String, Object> secrets = new HashMap<>();
        secrets.put("database_url", datasourceUrl);
        secrets.put("database_username", datasourceUsername);
        secrets.put("database_password", datasourcePassword);
        secrets.put("admin_username", "admin");
        secrets.put("admin_password", "admin123"); // VULNERABILITY: Hardcoded credentials
        secrets.put("api_key", "sk_live_51234567890abcdef"); // VULNERABILITY: Exposed API key
        secrets.put("jwt_secret", "my-super-secret-key-12345"); // VULNERABILITY: Exposed JWT secret
        secrets.put("encryption_key", "AES256-KEY-NOT-SO-SECRET");
        secrets.put("internal_api_endpoint", "http://internal-api.company.local/admin");
        secrets.put("aws_access_key", "AKIAIOSFODNN7EXAMPLE");
        secrets.put("aws_secret_key", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");
        
        Map<String, Object> response = new HashMap<>();
        response.put("warning", "SECURITY VULNERABILITY: This endpoint should require authentication!");
        response.put("secrets", secrets);
        response.put("timestamp", System.currentTimeMillis());
        
        log.debug("Sensitive data exposed: {}", secrets);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
