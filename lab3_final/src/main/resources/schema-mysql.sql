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
