INSERT INTO users (id, email, password, role, created_at, updated_at) VALUES

(UUID_TO_BIN(UUID()), 'user1@example.com', '$2a$12$J.0l5EUtl3Uw8tFc8B6uXeMdAKQsyxLNFjo4NeHoBxgxL0p8okEoa', 'USER', NOW(), NOW()),
(UUID_TO_BIN(UUID()), 'user2@example.com', '$2a$12$J.0l5EUtl3Uw8tFc8B6uXeMdAKQsyxLNFjo4NeHoBxgxL0p8okEoa', 'USER', NOW(), NOW()),
(UUID_TO_BIN(UUID()), 'user3@example.com', '$2a$12$J.0l5EUtl3Uw8tFc8B6uXeMdAKQsyxLNFjo4NeHoBxgxL0p8okEoa', 'USER', NOW(), NOW()),
(UUID_TO_BIN(UUID()), 'user4@example.com', '$2a$12$J.0l5EUtl3Uw8tFc8B6uXeMdAKQsyxLNFjo4NeHoBxgxL0p8okEoa', 'USER', NOW(), NOW()),
(UUID_TO_BIN(UUID()), 'user5@example.com', '$2a$12$J.0l5EUtl3Uw8tFc8B6uXeMdAKQsyxLNFjo4NeHoBxgxL0p8okEoa', 'USER', NOW(), NOW()),
(UUID_TO_BIN(UUID()), 'admin@example.com', '$2a$12$J.0l5EUtl3Uw8tFc8B6uXeMdAKQsyxLNFjo4NeHoBxgxL0p8okEoa', 'ADMIN', NOW(), NOW());
