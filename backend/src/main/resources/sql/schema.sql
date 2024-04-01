CREATE DATABASE IF NOT EXISTS `simonsays` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `simonsays`;
SET NAMES utf8;
SET time_zone = '+00:00';
SET foreign_key_checks = 0;

-- Schema for Users table
CREATE TABLE IF NOT EXISTS event
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    numberOfTables    INT NOT NULL
);

CREATE TABLE IF NOT EXISTS ingredient
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
    );

-- Schema for Posts table
CREATE TABLE IF NOT EXISTS posts
(
    id      INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT          NOT NULL,
    title   VARCHAR(255) NOT NULL,
    content TEXT
    -- FOREIGN KEY (user_id) REFERENCES users (id)
);

-- Insert seed data into 'users' table

-- Insert seed data into 'posts' table

