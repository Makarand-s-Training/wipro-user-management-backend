package com.demo.usermanagement.repository;

import com.demo.usermanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for User entity operations.
 *
 * <p>VULNERABILITY: Contains SQL injection vulnerability in searchUsersByName method.
 *
 * <p>Example usage:
 * <pre>
 * List&lt;User&gt; users = userRepository.findAll();
 * List&lt;User&gt; searchResults = userRepository.searchUsersByName("John");
 * // VULNERABLE: userRepository.searchUsersByName("admin' OR '1'='1")
 * </pre>
 *
 * @author Demo Application
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email address.
     *
     * @param email the email to search for
     * @return User with the specified email, or null if not found
     */
    User findByEmail(String email);

    /**
     * Search users by name using SQL query.
     *
     * <p><strong>VULNERABILITY: SQL INJECTION</strong>
     * <p>This method concatenates user input directly into SQL query,
     * allowing attackers to inject malicious SQL code.
     *
     * <p>Attack example:
     * <pre>
     * searchUsersByName("admin' OR '1'='1")
     * // Resulting query: SELECT * FROM users WHERE name LIKE '%admin' OR '1'='1%'
     * // This returns ALL users instead of filtering by name
     * </pre>
     *
     * @param name the name pattern to search for (UNSAFE - not parameterized)
     * @return List of users matching the query
     */
    @Query(value = "SELECT * FROM users WHERE name LIKE '%" + 
                   "' || ?1 || '" + 
                   "%'", nativeQuery = true)
    List<User> searchUsersByName(String name);
}
