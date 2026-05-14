package com.demo.usermanagement.controller;

import com.demo.usermanagement.model.User;
import com.demo.usermanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for User management operations.
 *
 * <p>TECHNICAL DEBT: Uses field injection instead of constructor injection.
 * <p>VULNERABILITIES: Contains multiple security vulnerabilities for demo purposes.
 *
 * <p>Example API usage:
 * <pre>
 * POST /users - Create new user
 * GET /users - List all users
 * GET /users/{id} - Get user by ID
 * PUT /users/{id} - Update user
 * DELETE /users/{id} - Delete user
 * GET /users/search?name={name} - Search users (VULNERABLE to SQL injection)
 * GET /users/{id}/profile - Get user profile HTML (VULNERABLE to XSS)
 * GET /admin/secrets - Get sensitive config (VULNERABLE - no auth required)
 * GET /slow - Slow endpoint for testing
 * </pre>
 *
 * @author Demo Application
 * @version 1.0
 */
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", maxAge = 3600)  // VULNERABILITY: Allow all origins
@Slf4j
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    // TECHNICAL DEBT: Field injection instead of constructor injection
    @Autowired
    private UserService userService;

    /**
     * Creates a new user.
     *
     * <p>VULNERABILITY: No authentication required to create users.
     * <p>VULNERABILITY: No input validation for email format or password strength.
     *
     * @param user the user to create
     * @return the created user with HTTP 201 status
     */
    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user in the system")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        log.info("POST /users - Creating user: {}", user.getEmail());
        User createdUser = userService.createUser(user);
        
        if (createdUser != null) {
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } else {
            // TECHNICAL DEBT: Poor error handling - returning null body
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieves all users.
     *
     * <p>VULNERABILITY: No authentication required, exposes all user data including passwords.
     *
     * @return list of all users
     */
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves all users from the system")
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("GET /users - Fetching all users");
        List<User> users = userService.getAllUsers();
        // VULNERABILITY: Returning users with plain text passwords
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Retrieves a user by ID.
     *
     * <p>VULNERABILITY: No authentication required to view user details.
     *
     * @param id the user ID
     * @return the user if found, 404 otherwise
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a specific user by their ID")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        log.info("GET /users/{} - Fetching user", id);
        User user = userService.getUserById(id);
        
        if (user != null) {
            // VULNERABILITY: Returning user with plain text password
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Updates an existing user.
     *
     * <p>VULNERABILITY: No authentication required, anyone can update any user.
     *
     * @param id the ID of the user to update
     * @param userDetails the updated user details
     * @return the updated user, or 404 if not found
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates an existing user")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        log.info("PUT /users/{} - Updating user", id);
        User updatedUser = userService.updateUser(id, userDetails);
        
        if (updatedUser != null) {
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes a user by ID.
     *
     * <p>VULNERABILITY: No authentication required, anyone can delete any user.
     *
     * @param id the ID of the user to delete
     * @return 204 if deleted, 404 if not found
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user from the system")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /users/{} - Deleting user", id);
        boolean deleted = userService.deleteUser(id);
        
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Searches users by name pattern.
     *
     * <p><strong>VULNERABILITY: SQL INJECTION</strong>
     * <p>This endpoint is vulnerable to SQL injection attacks.
     * User input is passed directly to a native SQL query without sanitization.
     *
     * <p>Attack example:
     * <pre>
     * GET /users/search?name=admin' OR '1'='1
     * This will return all users in the database
     * </pre>
     *
     * @param name the name pattern to search (UNSAFE - vulnerable to SQL injection)
     * @return list of users matching the pattern
     */
    @GetMapping("/search")
    @Operation(summary = "Search users by name (VULNERABLE)", 
               description = "Search users by name - VULNERABLE to SQL injection")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String name) {
        log.info("GET /users/search?name={} - Searching users", name);
        // VULNERABILITY: No input sanitization - vulnerable to SQL injection
        List<User> users = userService.searchUsersByName(name);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Returns user profile as HTML.
     *
     * <p><strong>VULNERABILITY: CROSS-SITE SCRIPTING (XSS)</strong>
     * <p>This endpoint returns user data embedded in HTML without sanitization.
     *
     * <p>Attack example:
     * <pre>
     * If a user's name contains: &lt;script&gt;alert('XSS')&lt;/script&gt;
     * The script will execute when the profile is viewed
     * </pre>
     *
     * @param id the user ID
     * @return HTML page with user profile (UNSAFE - vulnerable to XSS)
     */
    @GetMapping(value = "/{id}/profile", produces = MediaType.TEXT_HTML_VALUE)
    @Operation(summary = "Get user profile HTML (VULNERABLE)", 
               description = "Returns user profile as HTML - VULNERABLE to XSS")
    public ResponseEntity<String> getUserProfile(@PathVariable Long id) {
        log.info("GET /users/{}/profile - Fetching user profile", id);
        User user = userService.getUserById(id);
        
        if (user != null) {
            // VULNERABILITY: XSS - No HTML escaping of user input
            String html = "<html><body>" +
                         "<h1>User Profile</h1>" +
                         "<p>Name: " + user.getName() + "</p>" +  // UNSAFE!
                         "<p>Email: " + user.getEmail() + "</p>" +  // UNSAFE!
                         "<p>Role: " + user.getRole() + "</p>" +  // UNSAFE!
                         "</body></html>";
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(html);
        } else {
            return new ResponseEntity<>("<html><body><h1>User not found</h1></body></html>", 
                                       HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Simulates a slow API endpoint.
     *
     * <p>TECHNICAL DEBT: Uses Thread.sleep which blocks the thread.
     * Should use async processing instead.
     *
     * @return a delayed response
     * @throws InterruptedException if sleep is interrupted
     */
    @GetMapping("/slow")
    @Operation(summary = "Slow endpoint", 
               description = "Simulates a slow API response (5 second delay)")
    public ResponseEntity<Map<String, String>> slowEndpoint() throws InterruptedException {
        log.info("GET /users/slow - Slow endpoint called");
        // TECHNICAL DEBT: Blocking operation - should use async processing
        Thread.sleep(5000); // 5 second delay
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "This endpoint took 5 seconds to respond");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
