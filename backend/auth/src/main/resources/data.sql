-- Passwords are BCrypt-encoded. Default password for all users: "password"
MERGE INTO users (id, username, password) KEY (username) VALUES
    (1, 'admin', '$2b$10$MSAHjjxZ318SBSo01z0D4ukNt4gGInZZjuBl07pFzKSFczQxoCRHm'),
    (2, 'user',  '$2b$10$MSAHjjxZ318SBSo01z0D4ukNt4gGInZZjuBl07pFzKSFczQxoCRHm');
