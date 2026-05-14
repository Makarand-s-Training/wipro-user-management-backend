package com.demo.usermanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User entity representing a system user.
 *
 * <p>VULNERABILITY: Passwords are stored in plain text without encryption.
 * <p>TECHNICAL DEBT: Exposes entity directly to API layer without DTO.
 *
 * <p>Example usage:
 * <pre>
 * User user = new User();
 * user.setName("John Doe");
 * user.setEmail("john@example.com");
 * user.setPassword("password123"); // VULNERABILITY: Plain text password
 * user.setRole("USER");
 * </pre>
 *
 * @author Demo Application
 * @version 1.0
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User's full name.
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * User's email address.
     * VULNERABILITY: No email format validation
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * User's password.
     * VULNERABILITY: Stored in plain text without hashing
     */
    @Column(nullable = false, length = 100)
    private String password;

    /**
     * User's role in the system.
     * VULNERABILITY: No role validation, accepts any string
     */
    @Column(nullable = false, length = 50)
    private String role;
}
