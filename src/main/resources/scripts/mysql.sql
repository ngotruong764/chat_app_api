--  Create user_login table
CREATE TABLE user_login(
                           id BIGINT UNSIGNED AUTO_INCREMENT,
                           login_name VARCHAR(30),
                           email VARCHAR(255),
                           password VARCHAR(255),
                           confirmation_token VARCHAR(1000),
                           token_create_at TIMESTAMP,
                           refresh_token VARCHAR(1000),
                           refresh_token_at TIMESTAMP,
                           is_active BOOL DEFAULT FALSE,
                           is_reported BOOL DEFAULT FALSE,
                           is_blocked BOOL DEFAULT FALSE,
                           created_at TIMESTAMP,
                           updated_at TIMESTAMP,
                           PRIMARY KEY (id)
);
-- Create table user_info
CREATE TABLE user_info(
                          id BIGINT UNSIGNED AUTO_INCREMENT,
                          user_login_id BIGINT UNSIGNED,
                          first_name VARCHAR(30),
                          last_name VARCHAR(30),
                          user_name VARCHAR(30),
                          phone_number VARCHAR(10),
                          sex BOOL,
                          status BOOL,
                          profile_picture VARCHAR(300),
                          create_at TIMESTAMP,
                          updated_at TIMESTAMP,
                          role ENUM('USER', 'ADMIN'),
                          PRIMARY KEY (id),
                          FOREIGN KEY (user_login_id) REFERENCES user_login(id)
);