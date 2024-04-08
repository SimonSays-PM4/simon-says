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

CREATE TABLE IF NOT EXISTS menu_item
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    event_id INT NOT NULL,
    FOREIGN KEY (event_id) REFERENCES event (id)
    );

CREATE TABLE IF NOT EXISTS ingredient
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    event_id INT NOT NULL,
    FOREIGN KEY (event_id) REFERENCES event (id)
    );

CREATE TABLE IF NOT EXISTS menu_item_ingredients
(
    menu_item_id INT NOT NULL,
    FOREIGN KEY (menu_item_id) REFERENCES menu_item (id),
    ingredient_id INT NOT NULL,
    FOREIGN KEY (ingredient_id) REFERENCES ingredient (id)
);

