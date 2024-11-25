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

CREATE TABLE user_info(
                          id BIGINT UNSIGNED UNIQUE AUTO_INCREMENT,
                          email VARCHAR(255) UNIQUE,
                          username VARCHAR(100) UNIQUE,
                          password VARCHAR(255),
                          verification_code VARCHAR(255),
                          code_create_at TIMESTAMP,
                          is_active BOOL DEFAULT FALSE,
                          first_name VARCHAR(100),
                          last_name VARCHAR(100),
                          phone_number VARCHAR(10) UNIQUE,
                          sex ENUM('MALE', 'FEMALE', 'OTHERS'),
                          status BOOL DEFAULT FALSE,
                          profile_picture VARCHAR(500),
                          create_at TIMESTAMP,
                          update_at TIMESTAMP,
                          device_token VARCHAR(1000),
                          role ENUM( 'USER', 'ADMIN'),
                          dob DATE,
                          PRIMARY KEY (id)
);

CREATE TABLE friend (
                        id BIGINT UNSIGNED UNIQUE AUTO_INCREMENT,
                        uid1 BIGINT UNSIGNED,
                        uid2 BIGINT UNSIGNED,
                        createAt timestamp,
                        updateAt timestamp,
                        status ENUM('REQ_UID1', 'REQ_UID2', 'FRIEND', 'REJECT', 'BLOCK'),
                        PRIMARY KEY (id),
                        FOREIGN KEY (uid1) REFERENCES user_info(id),
                        FOREIGN KEY (uid2) REFERENCES user_info(id)
);