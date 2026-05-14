package com.demo.usermanagement.service;

import com.demo.usermanagement.model.User;
import com.demo.usermanagement.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for User management operations.
 *
 * <p>TECHNICAL DEBT:
 * <ul>
 *   <li>Uses field injection instead of constructor injection
 *   <li>Contains duplicated validation logic
 *   <li>Has methods exceeding 50 lines
 *   <li>Poor exception handling (swallows exceptions)
 *   <li>Multiple TODO comments indicating needed refactoring
 * </ul>
 *
 * <p>Example usage:
 * <pre>
 * User user = new User(null, "John Doe", "john@example.com", "password", "USER");
 * User created = userService.createUser(user);
 * </pre>
 *
 * @author Demo Application
 * @version 1.0
 */
@Service
@Slf4j
public class UserService {

    // TECHNICAL DEBT: Field injection instead of constructor injection
    @Autowired
    private UserRepository userRepository;

    /**
     * Creates a new user in the system.
     *
     * <p>TECHNICAL DEBT: Duplicated validation logic, should be extracted to separate method.
     * <p>VULNERABILITY: No password strength validation.
     *
     * @param user the user to create
     * @return the created user with generated ID
     */
    public User createUser(User user) {
        log.info("Creating new user: {}", user.getEmail());
        
        // TODO: Extract validation to separate method to avoid duplication
        // TECHNICAL DEBT: Duplicated validation logic
        if (user == null) {
            log.error("User object is null");
            return null; // TECHNICAL DEBT: Should throw exception instead
        }
        
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            log.error("User name is empty");
            return null;
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            log.error("User email is empty");
            return null;
        }
        
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            log.error("User password is empty");
            return null;
        }
        
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            log.error("User role is empty");
            return null;
        }
        
        // VULNERABILITY: No check for existing email
        try {
            User savedUser = userRepository.save(user);
            log.info("User created successfully with ID: {}", savedUser.getId());
            // VULNERABILITY: Logging sensitive password
            log.debug("User details: email={}, password={}, role={}", 
                     savedUser.getEmail(), savedUser.getPassword(), savedUser.getRole());
            return savedUser;
        } catch (Exception e) {
            // TECHNICAL DEBT: Poor exception handling - just logging and returning null
            log.error("Error creating user", e);
            return null;
        }
    }

    /**
     * Retrieves all users from the system.
     *
     * @return list of all users
     */
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        try {
            List<User> users = userRepository.findAll();
            log.info("Found {} users", users.size());
            // VULNERABILITY: Logging all user passwords
            users.forEach(u -> log.debug("User: {} - Password: {}", u.getEmail(), u.getPassword()));
            return users;
        } catch (Exception e) {
            // TECHNICAL DEBT: Swallowing exception
            log.error("Error fetching users", e);
            return List.of();
        }
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id the user ID
     * @return the user if found, null otherwise
     */
    public User getUserById(Long id) {
        log.info("Fetching user with ID: {}", id);
        try {
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                log.debug("User found: {}", user.get().getEmail());
                return user.get();
            } else {
                log.warn("User not found with ID: {}", id);
                return null; // TECHNICAL DEBT: Should throw exception
            }
        } catch (Exception e) {
            log.error("Error fetching user", e);
            return null;
        }
    }

    /**
     * Updates an existing user.
     *
     * <p>TECHNICAL DEBT: This method is too long (>50 lines) and does too much.
     * Should be refactored into smaller methods.
     *
     * @param id the ID of the user to update
     * @param userDetails the updated user details
     * @return the updated user, or null if not found
     */
    public User updateUser(Long id, User userDetails) {
        log.info("Updating user with ID: {}", id);
        
        // TODO: Refactor this method - it's too long and complex
        // TECHNICAL DEBT: Duplicated validation logic (same as createUser)
        if (userDetails == null) {
            log.error("User details object is null");
            return null;
        }
        
        if (userDetails.getName() == null || userDetails.getName().trim().isEmpty()) {
            log.error("User name is empty");
            return null;
        }
        
        if (userDetails.getEmail() == null || userDetails.getEmail().trim().isEmpty()) {
            log.error("User email is empty");
            return null;
        }
        
        if (userDetails.getPassword() == null || userDetails.getPassword().trim().isEmpty()) {
            log.error("User password is empty");
            return null;
        }
        
        if (userDetails.getRole() == null || userDetails.getRole().trim().isEmpty()) {
            log.error("User role is empty");
            return null;
        }
        
        try {
            Optional<User> existingUserOpt = userRepository.findById(id);
            
            if (!existingUserOpt.isPresent()) {
                log.warn("User not found with ID: {}", id);
                return null;
            }
            
            User existingUser = existingUserOpt.get();
            
            // TODO: Extract this update logic to separate method
            existingUser.setName(userDetails.getName());
            existingUser.setEmail(userDetails.getEmail());
            existingUser.setPassword(userDetails.getPassword()); // VULNERABILITY: Password updated without hashing
            existingUser.setRole(userDetails.getRole());
            
            User updatedUser = userRepository.save(existingUser);
            log.info("User updated successfully: {}", updatedUser.getEmail());
            log.debug("Updated password: {}", updatedUser.getPassword()); // VULNERABILITY: Logging password
            
            return updatedUser;
        } catch (Exception e) {
            // TECHNICAL DEBT: Generic exception handling
            log.error("Error updating user", e);
            return null;
        }
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        try {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
                log.info("User deleted successfully with ID: {}", id);
                return true;
            } else {
                log.warn("User not found with ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            // TECHNICAL DEBT: Swallowing exception
            log.error("Error deleting user", e);
            return false;
        }
    }

    /**
     * Searches users by name pattern.
     *
     * <p>VULNERABILITY: This method uses the vulnerable repository method
     * that is susceptible to SQL injection.
     *
     * @param name the name pattern to search (UNSAFE - vulnerable to SQL injection)
     * @return list of users matching the pattern
     */
    public List<User> searchUsersByName(String name) {
        log.info("Searching users by name: {}", name);
        // VULNERABILITY: No input sanitization before passing to vulnerable repository method
        try {
            List<User> results = userRepository.searchUsersByName(name);
            log.info("Found {} users matching name pattern", results.size());
            return results;
        } catch (Exception e) {
            log.error("Error searching users", e);
            return List.of();
        }
    }

    // TODO: Add method to validate email format
    // TODO: Add method to validate password strength
    // TODO: Add method to hash passwords
    // TODO: Extract common validation logic to reduce duplication
    // TODO: Implement proper exception handling with custom exceptions
}
