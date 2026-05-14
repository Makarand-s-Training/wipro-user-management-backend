# User Management System (Intentionally Vulnerable Demo)

⚠️ **WARNING: THIS APPLICATION CONTAINS INTENTIONAL SECURITY VULNERABILITIES AND POOR CODING PRACTICES** ⚠️

**DO NOT USE IN PRODUCTION!**

This is a demonstration application designed for security training and code quality awareness. It showcases common vulnerabilities and technical debt patterns found in real-world applications.

## Purpose

This application serves as a teaching tool to demonstrate:
- Common security vulnerabilities (OWASP Top 10)
- Technical debt and code smells
- Poor coding practices that should be avoided
- The importance of secure coding practices

## Table of Contents

- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Known Security Vulnerabilities](#known-security-vulnerabilities)
- [Technical Debt](#technical-debt)
- [Endpoints](#endpoints)
- [Testing Vulnerabilities](#testing-vulnerabilities)
- [Docker Support](#docker-support)
- [Database Access](#database-access)
- [Configuration](#configuration)

## Prerequisites

- **Java 17** or higher
- **Maven 3.8+**
- **Docker** (optional, for containerized deployment)

## Quick Start

```bash
# Clone the repository
cd user-management-backend

# Build the application
mvn clean package

# Run the application
java -jar target/user-management.jar

# Or run with Maven
mvn spring-boot:run
```

The application will start on **http://localhost:8080**

## Running the Application

### Option 1: Using Maven

```bash
mvn spring-boot:run
```

### Option 2: Using JAR file

```bash
mvn clean package
java -jar target/user-management.jar
```

### Option 3: Custom Port

```bash
# Windows
set PORT=9090
java -jar target/user-management.jar

# Linux/Mac
PORT=9090 java -jar target/user-management.jar
```

### Option 4: Using Docker

```bash
# Build Docker image
docker build -t user-management:vulnerable .

# Run container
docker run -p 8080:8080 user-management:vulnerable

# Run with custom port
docker run -p 9090:8080 -e PORT=8080 user-management:vulnerable
```

## API Documentation

Once the application is running, access:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## Known Security Vulnerabilities

This application contains the following **INTENTIONAL** security vulnerabilities:

### 1. SQL Injection (CWE-89)

**Location**: `GET /users/search?name={name}`

**Description**: The search endpoint concatenates user input directly into SQL query without parameterization.

**Exploitation Example**:
```bash
# Returns all users instead of filtering
curl "http://localhost:8080/users/search?name=admin' OR '1'='1"
```

**Files**: 
- `UserRepository.java` - Vulnerable `@Query` annotation
- `UserController.java` - `/search` endpoint

### 2. Cross-Site Scripting (XSS) (CWE-79)

**Location**: `GET /users/{id}/profile`

**Description**: User profile endpoint returns HTML with unsanitized user input.

**Exploitation Example**:
```bash
# Create user with malicious name
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"name":"<script>alert(\"XSS\")</script>","email":"xss@test.com","password":"pass","role":"USER"}'

# View profile - script executes in browser
# Visit: http://localhost:8080/users/{id}/profile
```

**Files**:
- `UserController.java` - `/users/{id}/profile` endpoint

### 3. Hardcoded Credentials (CWE-798)

**Location**: `SecurityConfig.java`, `data.sql`

**Description**: Application contains hardcoded usernames and passwords in source code.

**Credentials**:
```
Username: admin  | Password: admin123
Username: user   | Password: user123
Username: demo   | Password: demo
```

**Files**:
- `SecurityConfig.java` - Hardcoded Spring Security users
- `data.sql` - Database initialization with hardcoded passwords

### 4. Missing Password Encryption (CWE-256)

**Location**: Entire application

**Description**: Passwords are stored and transmitted in plain text without any hashing or encryption.

**Impact**: All user passwords are visible in:
- Database (H2 console)
- API responses
- Application logs

**Files**:
- `User.java` - No password encoding
- `SecurityConfig.java` - Uses `{noop}` prefix (no password encoder)
- `data.sql` - Plain text passwords

### 5. Disabled CSRF Protection (CWE-352)

**Location**: `SecurityConfig.java`

**Description**: Cross-Site Request Forgery protection is explicitly disabled.

**Impact**: Application is vulnerable to CSRF attacks where malicious sites can trigger actions on behalf of authenticated users.

**Files**:
- `SecurityConfig.java` - `.csrf(csrf -> csrf.disable())`

### 6. Broken Access Control (CWE-284)

**Location**: All endpoints

**Description**: No authentication or authorization required for any endpoint, including admin functions.

**Exploitation Example**:
```bash
# Anyone can access admin secrets without authentication
curl http://localhost:8080/admin/secrets

# Anyone can delete any user
curl -X DELETE http://localhost:8080/users/1
```

**Files**:
- `SecurityConfig.java` - `.anyRequest().permitAll()`
- `AdminController.java` - No authentication required

### 7. Sensitive Data Exposure (CWE-200)

**Location**: `GET /admin/secrets`

**Description**: Endpoint exposes sensitive configuration data without authentication.

**Exposed Data**:
- Database credentials
- API keys
- JWT secrets
- AWS credentials
- Internal endpoints

**Exploitation Example**:
```bash
curl http://localhost:8080/admin/secrets
```

**Files**:
- `AdminController.java` - `/admin/secrets` endpoint

### 8. Information Disclosure via Logging (CWE-532)

**Location**: `UserService.java`

**Description**: Sensitive data (passwords, user details) logged at DEBUG level.

**Impact**: Passwords and sensitive information exposed in application logs.

**Files**:
- `UserService.java` - Multiple DEBUG log statements with passwords

### 9. Excessive Data Exposure (CWE-213)

**Location**: All user endpoints

**Description**: API returns complete user objects including passwords in responses.

**Exploitation Example**:
```bash
# Passwords visible in response
curl http://localhost:8080/users
```

**Files**:
- `UserController.java` - Returns full User entity
- No DTO layer to filter sensitive data

### 10. H2 Console Enabled in Production (CWE-215)

**Location**: `application.properties`

**Description**: H2 database console accessible without authentication.

**Access**: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

**Files**:
- `application.properties` - `spring.h2.console.enabled=true`

## Technical Debt

This application demonstrates common technical debt and code quality issues:

### 1. Field Injection (Anti-pattern)

**Location**: `UserService.java`, `UserController.java`

**Issue**: Uses `@Autowired` field injection instead of constructor injection.

**Impact**:
- Harder to test
- Violates immutability
- Hidden dependencies

**Example**:
```java
// BAD (used in this app)
@Autowired
private UserRepository userRepository;

// GOOD (should be)
private final UserRepository userRepository;
public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
}
```

### 2. Code Duplication

**Location**: `UserService.java`

**Issue**: Validation logic duplicated in `createUser()` and `updateUser()` methods.

**Impact**:
- Maintenance burden
- Inconsistency risk
- Violates DRY principle

**TODO Comments**: Multiple TODOs indicating needed refactoring

### 3. Long Methods

**Location**: `UserService.updateUser()`

**Issue**: Method exceeds 50 lines and performs multiple responsibilities.

**Impact**:
- Reduced readability
- Difficult to test
- Violates Single Responsibility Principle

### 4. Poor Exception Handling

**Location**: Throughout `UserService.java`

**Issues**:
- Generic exception catching
- Exceptions swallowed (just logged)
- Returns null instead of throwing exceptions

**Impact**:
- Difficult to debug
- Unexpected null pointer exceptions
- Lost error context

**Example**:
```java
try {
    // operation
} catch (Exception e) {
    log.error("Error", e);
    return null; // BAD - should throw or handle properly
}
```

### 5. No DTO Layer

**Location**: Entire application

**Issue**: Exposes JPA entities directly to API layer.

**Impact**:
- Exposes internal structure
- Cannot filter sensitive fields
- Tight coupling between layers

### 6. No Input Validation

**Location**: Controllers

**Issue**: No validation annotations or input sanitization.

**Impact**:
- Invalid data can enter system
- No email format validation
- No password strength requirements

### 7. Blocking Operations

**Location**: `UserController.slowEndpoint()`

**Issue**: Uses `Thread.sleep()` which blocks server threads.

**Impact**:
- Poor scalability
- Thread pool exhaustion under load
- Should use async/reactive programming

### 8. No Unit Tests

**Issue**: Only minimal context load test, no real unit tests.

**Impact**:
- No safety net for refactoring
- Unknown code coverage
- Higher risk of bugs

## Endpoints

### User Management

| Method | Endpoint | Description | Vulnerability |
|--------|----------|-------------|---------------|
| POST | `/users` | Create new user | No auth, no validation |
| GET | `/users` | List all users | No auth, exposes passwords |
| GET | `/users/{id}` | Get user by ID | No auth, exposes password |
| PUT | `/users/{id}` | Update user | No auth, anyone can update |
| DELETE | `/users/{id}` | Delete user | No auth, anyone can delete |
| GET | `/users/search?name={name}` | Search users | **SQL Injection** |
| GET | `/users/{id}/profile` | User profile HTML | **XSS** |
| GET | `/users/slow` | Slow endpoint (5s delay) | Performance issue |

### Admin (Vulnerable)

| Method | Endpoint | Description | Vulnerability |
|--------|----------|-------------|---------------|
| GET | `/admin/secrets` | Get sensitive config | **No auth required!** |

### Actuator

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Health check |
| `/actuator/info` | Application info |
| `/actuator/metrics` | Application metrics |

### Documentation

| Endpoint | Description |
|----------|-------------|
| `/swagger-ui.html` | Swagger UI |
| `/api-docs` | OpenAPI JSON |
| `/h2-console` | H2 Database Console |

## Testing Vulnerabilities

### Test SQL Injection

```bash
# Normal search
curl "http://localhost:8080/users/search?name=John"

# SQL Injection - returns all users
curl "http://localhost:8080/users/search?name=admin' OR '1'='1"

# SQL Injection - alternative
curl "http://localhost:8080/users/search?name=' OR 1=1--"
```

### Test XSS

```bash
# 1. Create user with malicious name
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "<script>alert(\"XSS Attack!\")</script>",
    "email": "hacker@test.com",
    "password": "password",
    "role": "USER"
  }'

# 2. Get the user ID from response

# 3. Visit in browser: http://localhost:8080/users/{id}/profile
# The JavaScript will execute
```

### Test Sensitive Data Exposure

```bash
# Access admin secrets without authentication
curl http://localhost:8080/admin/secrets

# View all user passwords
curl http://localhost:8080/users
```

### Test No Authentication

```bash
# Delete any user without authentication
curl -X DELETE http://localhost:8080/users/1

# Update any user's password without authentication
curl -X PUT http://localhost:8080/users/2 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Hacked User",
    "email": "hacked@test.com",
    "password": "newpassword",
    "role": "ADMIN"
  }'
```

## Docker Support

### Build and Run

```bash
# Build image
docker build -t user-management:vulnerable .

# Run container
docker run -p 8080:8080 user-management:vulnerable

# Run with environment variables
docker run -p 9090:8080 \
  -e PORT=8080 \
  user-management:vulnerable

# Run in detached mode
docker run -d -p 8080:8080 --name user-mgmt user-management:vulnerable

# View logs
docker logs user-mgmt

# Stop container
docker stop user-mgmt
```

### Docker Compose (Optional)

Create `docker-compose.yml`:

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - PORT=8080
```

Run with:
```bash
docker-compose up
```

## Database Access

### H2 Console

Access the H2 console at: http://localhost:8080/h2-console

**Connection Settings**:
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: (leave empty)

**Pre-populated Users**:

| ID | Name | Email | Password | Role |
|----|------|-------|----------|------|
| 1 | Admin User | admin@demo.com | admin123 | ADMIN |
| 2 | John Doe | john.doe@demo.com | password123 | USER |
| 3 | Jane Smith | jane.smith@demo.com | qwerty | USER |
| 4 | Bob Manager | bob.manager@demo.com | manager@2024 | MANAGER |
| 5 | Alice Developer | alice.dev@demo.com | 12345 | DEVELOPER |

## Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| PORT | 8080 | Application port |

### Application Properties

Key configurations in `application.properties`:

```properties
# Server
server.port=${PORT:8080}

# Database
spring.datasource.url=jdbc:h2:mem:testdb

# H2 Console (VULNERABILITY!)
spring.h2.console.enabled=true

# Logging (VULNERABILITY - DEBUG level)
logging.level.com.demo.usermanagement=DEBUG
```

## Project Structure

```
user-management-backend/
├── src/
│   ├── main/
│   │   ├── java/com/demo/usermanagement/
│   │   │   ├── UserManagementApplication.java
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AdminController.java
│   │   │   │   └── UserController.java
│   │   │   ├── model/
│   │   │   │   └── User.java
│   │   │   ├── repository/
│   │   │   │   └── UserRepository.java
│   │   │   └── service/
│   │   │       └── UserService.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── data.sql
│   └── test/
├── Dockerfile
├── .dockerignore
├── .gitignore
├── pom.xml
└── README.md
```

## Technologies Used

- **Spring Boot 3.2.5** - Application framework
- **Java 17** - Programming language
- **Maven** - Build tool
- **H2 Database** - In-memory database
- **Spring Data JPA** - Data access
- **Spring Security** - Security framework (intentionally misconfigured)
- **SpringDoc OpenAPI** - API documentation (Swagger)
- **Spring Boot Actuator** - Monitoring and management
- **Lombok** - Boilerplate code reduction

## Learning Objectives

After exploring this application, you should understand:

1. **How SQL injection works** and how to prevent it (use parameterized queries)
2. **How XSS attacks work** and how to prevent them (sanitize/escape output)
3. **Why hardcoded credentials are dangerous** and where to store secrets securely
4. **Why password encryption is critical** and how to use BCrypt
5. **Why CSRF protection is important** and when to use it
6. **Why authentication/authorization is necessary** for all endpoints
7. **Why field injection is problematic** and when to use constructor injection
8. **Why code duplication is technical debt** and how to refactor
9. **Why proper exception handling matters** for debugging and security
10. **Why separating concerns (DTOs) is important** for maintainability

## Remediation Guide

### Fix SQL Injection

```java
// BEFORE (Vulnerable)
@Query(value = "SELECT * FROM users WHERE name LIKE '%" + "' || ?1 || '" + "%'", nativeQuery = true)
List<User> searchUsersByName(String name);

// AFTER (Secure)
@Query("SELECT u FROM User u WHERE u.name LIKE %:name%")
List<User> searchUsersByName(@Param("name") String name);
```

### Fix XSS

```java
// Use a template engine with auto-escaping (Thymeleaf)
// Or manually escape HTML
import org.apache.commons.text.StringEscapeUtils;

String safeName = StringEscapeUtils.escapeHtml4(user.getName());
```

### Fix Password Storage

```java
// Add to SecurityConfig
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// Use in service
String hashedPassword = passwordEncoder.encode(plainPassword);
```

### Fix Authentication

```java
// In SecurityConfig
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/admin/**").hasRole("ADMIN")
    .requestMatchers("/users/**").authenticated()
    .anyRequest().authenticated()
)
```

## Disclaimer

⚠️ **THIS APPLICATION IS FOR EDUCATIONAL PURPOSES ONLY** ⚠️

**DO NOT:**
- Deploy this application to production
- Expose this application to the internet
- Use this code as a template for real applications
- Store real user data in this application

**DO:**
- Use for security training and awareness
- Learn from the vulnerabilities
- Understand the importance of secure coding
- Practice identifying and fixing security issues

## License

This is a demonstration application for educational purposes.

## Contact

For questions about this demo application, please contact the training team.

---

**Remember**: Real applications should NEVER contain these vulnerabilities. Always follow security best practices and conduct regular security audits.
