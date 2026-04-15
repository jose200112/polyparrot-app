\c users_db;

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    first_surname VARCHAR(50) NOT NULL,
    second_surname VARCHAR(50),
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP,
    avatar_url VARCHAR(255)
);

INSERT INTO users (name, first_surname, second_surname, email, password, role, created_at) VALUES
('Carlos', 'García', 'López', 'carlos@polyparrot.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LzTf5iEeAbm', 'TEACHER', NOW()),
('María', 'Martínez', 'Sánchez', 'maria@polyparrot.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LzTf5iEeAbm', 'TEACHER', NOW()),
('Pierre', 'Dubois', NULL, 'pierre@polyparrot.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LzTf5iEeAbm', 'TEACHER', NOW()),
('Hans', 'Müller', NULL, 'hans@polyparrot.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LzTf5iEeAbm', 'TEACHER', NOW()),
('Giulia', 'Rossi', NULL, 'giulia@polyparrot.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LzTf5iEeAbm', 'TEACHER', NOW()),
('Yuki', 'Tanaka', NULL, 'yuki@polyparrot.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LzTf5iEeAbm', 'TEACHER', NOW()),
('Ana', 'Student', NULL, 'ana@polyparrot.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LzTf5iEeAbm', 'STUDENT', NOW());