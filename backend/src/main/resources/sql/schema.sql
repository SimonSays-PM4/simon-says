CREATE DATABASE IF NOT EXISTS `micha` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `micha`;
SET NAMES utf8;
SET time_zone = '+00:00';
SET foreign_key_checks = 0;

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

-- Insert seed data into 'users' table
INSERT INTO users (username, email)
VALUES ('johndoe', 'john@example.com');
INSERT INTO users (username, email)
VALUES ('janedoe', 'jane@example.com');

-- Insert seed data into 'posts' table
INSERT INTO posts (user_id, title, content)
VALUES (1, 'First Post', 'This is the content of the first post.');
INSERT INTO posts (user_id, title, content)
VALUES (2, 'Second Post', 'This is the content of the second post.');
