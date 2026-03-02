-- Initialize database schema (optional)
-- This file runs when MySQL container starts for the first time

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS retro;

USE retro;

-- Grant privileges
GRANT ALL PRIVILEGES ON retro.* TO 'retro_user'@'%';
FLUSH PRIVILEGES;

-- Optional: Create initial admin user (password: admin123)
-- Uncomment if you want a default admin user
-- INSERT INTO users (email, name, password, role) 
-- VALUES ('admin@retro.com', 'Admin User', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN');
