-- Schema for Users table
CREATE TABLE IF NOT EXISTS users
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email    VARCHAR(255) NOT NULL UNIQUE
);

-- Schema for Posts table
CREATE TABLE IF NOT EXISTS posts
(
    id      INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT          NOT NULL,
    title   VARCHAR(255) NOT NULL,
    content TEXT,
    FOREIGN KEY (user_id) REFERENCES users (id)
);
