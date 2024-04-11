DROP TABLE IF EXISTS product_category;
DROP TABLE IF EXISTS info;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS participant;
DROP TABLE IF EXISTS USER_AUTHORITY;
DROP TABLE IF EXISTS USER;
DROP TABLE IF EXISTS AUTHORITY;


CREATE TABLE Participant (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             last_name VARCHAR(50),
                             first_name VARCHAR(50),
                             birth_date TIMESTAMP
);

CREATE TABLE Category (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(50)
);

CREATE TABLE Info (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      photo LONGBLOB,
                      description TEXT,
                      product_id BIGINT
);

CREATE TABLE Product (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(50),
                         code VARCHAR(20),
                         reserve_price DOUBLE,
                         restored BOOLEAN,
                         seller_id BIGINT,
                         currency ENUM('USD', 'EUR', 'GBP')
);

ALTER TABLE Info ADD FOREIGN KEY (product_id) REFERENCES Product(id);

ALTER TABLE Product ADD FOREIGN KEY (seller_id) REFERENCES Participant(id);

CREATE TABLE product_category (
    category_id BIGINT,
    product_id BIGINT,
    FOREIGN KEY (category_id) REFERENCES Category(id),
    FOREIGN KEY (product_id) REFERENCES Product(id),
    PRIMARY KEY (category_id, product_id)
);

CREATE TABLE USER(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    account_non_expired BOOLEAN NOT NULL DEFAULT true,
    account_non_locked BOOLEAN NOT NULL DEFAULT true,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE AUTHORITY(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE USER_AUTHORITY(
    user_id BIGINT,
    authority_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES USER(id),
    FOREIGN KEY (authority_id) REFERENCES AUTHORITY(id),
    PRIMARY KEY (user_id, authority_id)
);