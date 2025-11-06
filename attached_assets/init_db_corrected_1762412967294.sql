-- init_db_corrected.sql
CREATE DATABASE IF NOT EXISTS ffr_stage CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE ffr_stage;

-- users
CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  role ENUM('root','user') NOT NULL DEFAULT 'user',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- categories
CREATE TABLE IF NOT EXISTS categories (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL UNIQUE,
  description VARCHAR(255)
);

-- annonces
CREATE TABLE IF NOT EXISTS annonces (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(150) NOT NULL,
  content TEXT,
  category_id BIGINT,
  audio_path VARCHAR(255),
  prop VARCHAR(100),
  type VARCHAR(50),
  nbr_dif INT DEFAULT 0,
  nbr_prev INT DEFAULT 0,
  created_by BIGINT,
  date_cre DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_upd DATETIME,
  FOREIGN KEY (category_id) REFERENCES categories(id),
  FOREIGN KEY (created_by) REFERENCES users(id)
);

-- programme (programmation)
CREATE TABLE IF NOT EXISTS programme (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  date_pro DATETIME,
  dif1 BOOLEAN DEFAULT FALSE,
  dif2 BOOLEAN DEFAULT FALSE,
  dif3 BOOLEAN DEFAULT FALSE,
  etat BOOLEAN DEFAULT FALSE,
  annonce_id BIGINT,
  nbr_dif INT DEFAULT 0,
  FOREIGN KEY (annonce_id) REFERENCES annonces(id)
);

