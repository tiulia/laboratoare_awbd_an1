DROP TABLE IF EXISTS user_authority;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS authority;


DROP TABLE IF EXISTS product_category;
DROP TABLE IF EXISTS info;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS participant;

CREATE TABLE participant (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             last_name VARCHAR(50),
                             first_name VARCHAR(50),
                             birth_date TIMESTAMP
);

CREATE TABLE category (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(50)
);

CREATE TABLE info (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      photo LONGBLOB,
                      description TEXT,
                      product_id BIGINT
);

CREATE TABLE product (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(50),
                         code VARCHAR(20),
                         reserve_price DOUBLE,
                         restored BOOLEAN,
                         seller_id BIGINT,
                         currency VARCHAR(5)
);

ALTER TABLE info ADD FOREIGN KEY (product_id) REFERENCES product(id);

ALTER TABLE product ADD FOREIGN KEY (seller_id) REFERENCES participant(id);

CREATE TABLE product_category (
                                  category_id BIGINT,
                                  product_id BIGINT,
                                  FOREIGN KEY (category_id) REFERENCES category(id),
                                  FOREIGN KEY (product_id) REFERENCES product(id),
                                  PRIMARY KEY (category_id, product_id)
);


CREATE TABLE user(
                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                     username VARCHAR(50) NOT NULL,
                     password VARCHAR(100) NOT NULL,
                     enabled BOOLEAN NOT NULL DEFAULT true,
                     account_non_expired BOOLEAN NOT NULL DEFAULT true,
                     account_non_locked BOOLEAN NOT NULL DEFAULT true,
                     credentials_non_expired BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE authority(
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          role VARCHAR(50) NOT NULL
);

CREATE TABLE user_authority(
                               user_id BIGINT,
                               authority_id BIGINT,
                               FOREIGN KEY (user_id) REFERENCES user(id),
                               FOREIGN KEY (authority_id) REFERENCES authority(id),
                               PRIMARY KEY (user_id, authority_id)
);