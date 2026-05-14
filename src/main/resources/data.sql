-- Sample Users for Demo
-- VULNERABILITY: Hardcoded credentials with plain text passwords

INSERT INTO users (id, name, email, password, role) VALUES 
(1, 'Admin User', 'admin@demo.com', 'admin123', 'ADMIN');

INSERT INTO users (id, name, email, password, role) VALUES 
(2, 'John Doe', 'john.doe@demo.com', 'password123', 'USER');

INSERT INTO users (id, name, email, password, role) VALUES 
(3, 'Jane Smith', 'jane.smith@demo.com', 'qwerty', 'USER');

INSERT INTO users (id, name, email, password, role) VALUES 
(4, 'Bob Manager', 'bob.manager@demo.com', 'manager@2024', 'MANAGER');

INSERT INTO users (id, name, email, password, role) VALUES 
(5, 'Alice Developer', 'alice.dev@demo.com', '12345', 'DEVELOPER');

-- VULNERABILITY NOTE:
-- All passwords are stored in plain text
-- Credentials are hardcoded in source control
-- Default admin password is weak and well-known
