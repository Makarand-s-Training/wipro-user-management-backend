package com.demo.usermanagement;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for User Management System.
 *
 * <p>
 * <strong>INTENTIONALLY VULNERABLE APPLICATION FOR DEMO/TRAINING
 * PURPOSES</strong>
 *
 * <p>
 * This application demonstrates common security vulnerabilities and technical
 * debt:
 * <ul>
 * <li>SQL Injection vulnerabilities
 * <li>Cross-Site Scripting (XSS) vulnerabilities
 * <li>Hardcoded credentials
 * <li>No password encryption
 * <li>Disabled CSRF protection
 * <li>Exposed sensitive data without authentication
 * <li>Poor exception handling
 * <li>Field injection instead of constructor injection
 * <li>Code duplication
 * <li>Long methods without refactoring
 * </ul>
 *
 * <p>
 * <strong>WARNING: DO NOT USE THIS APPLICATION IN PRODUCTION!</strong>
 * <p>
 * This is for educational and demonstration purposes only.
 *
 * <p>
 * Application runs on port configured via environment variable PORT or defaults
 * to 8080.
 *
 * <p>
 * Example startup:
 * 
 * <pre>
 * mvn clean package
 * java -jar target/user-management.jar
 * </pre>
 *
 * <p>
 * Access points:
 * <ul>
 * <li>API Documentation: http://localhost:8080/swagger-ui.html
 * <li>H2 Console: http://localhost:8080/h2-console
 * <li>Actuator Health: http://localhost:8080/actuator/health
 * </ul>
 *
 * @author Demo Application
 * @version 1.0
 */
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "User Management API (Vulnerable Demo)", version = "1.0", description = """
        <strong>⚠️ WARNING: INTENTIONALLY VULNERABLE APPLICATION ⚠️</strong>

        <p>This API contains deliberate security vulnerabilities and technical debt for demonstration purposes.</p>

        <h3>Known Vulnerabilities:</h3>
        <ul>
            <li>SQL Injection - /users/search endpoint</li>
            <li>Cross-Site Scripting (XSS) - /users/{id}/profile endpoint</li>
            <li>Hardcoded credentials in SecurityConfig</li>
            <li>No password encryption</li>
            <li>CSRF protection disabled</li>
            <li>Sensitive data exposure - /admin/secrets endpoint (no auth required)</li>
            <li>No authentication required for any endpoint</li>
        </ul>

        <h3>Technical Debt:</h3>
        <ul>
            <li>Field injection instead of constructor injection</li>
            <li>Duplicated validation logic</li>
            <li>Long methods without refactoring</li>
            <li>Poor exception handling</li>
            <li>Direct entity exposure (no DTOs)</li>
        </ul>

        <p><strong>DO NOT USE IN PRODUCTION!</strong></p>
        """, contact = @Contact(name = "Demo Application", email = "demo@example.com"))
// ,
// servers = {
// @Server(url = "http://localhost:8080", description = "Local Development
// Server"),
// @Server(url = "http://localhost:${PORT}", description = "Configurable Port
// Server")
// }
)
public class UserManagementApplication {

    /**
     * Main entry point for the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(UserManagementApplication.class, args);
        System.out.println("\n" +
                "═══════════════════════════════════════════════════════════════════════════\n" +
                "  ⚠️  INTENTIONALLY VULNERABLE APPLICATION - FOR DEMO PURPOSES ONLY  ⚠️\n" +
                "═══════════════════════════════════════════════════════════════════════════\n" +
                "  Application started successfully!\n" +
                "  \n" +
                "  📚 Swagger UI: http://localhost:8080/swagger-ui.html\n" +
                "  🗄️  H2 Console: http://localhost:8080/h2-console\n" +
                "  ❤️  Health Check: http://localhost:8080/actuator/health\n" +
                "  \n" +
                "  🔓 Security Credentials (Hardcoded - VULNERABILITY!):\n" +
                "     Username: admin | Password: admin123\n" +
                "     Username: user  | Password: user123\n" +
                "  \n" +
                "  ⚠️  Known Vulnerabilities:\n" +
                "     • SQL Injection: GET /users/search?name=admin' OR '1'='1\n" +
                "     • XSS: GET /users/{id}/profile\n" +
                "     • No Auth Required: GET /admin/secrets\n" +
                "     • CSRF Disabled, No Password Encryption\n" +
                "  \n" +
                "  ⚠️  DO NOT USE THIS APPLICATION IN PRODUCTION!\n" +
                "═══════════════════════════════════════════════════════════════════════════\n");
    }
}
