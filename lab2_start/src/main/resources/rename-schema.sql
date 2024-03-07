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
                      description TEXT
);

CREATE TABLE Product (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(50),
                         code VARCHAR(20),
                         reserve_price DOUBLE,
                         restored BOOLEAN,
                         info_id BIGINT,
                         seller_id BIGINT,
                         currency TINYINT
);

ALTER TABLE Product ADD FOREIGN KEY (info_id) REFERENCES Info(id);

ALTER TABLE Product ADD FOREIGN KEY (seller_id) REFERENCES Participant(id);

CREATE TABLE product_category (
                                  category_id BIGINT,
                                  product_id BIGINT,
                                  FOREIGN KEY (category_id) REFERENCES Category(id),
                                  FOREIGN KEY (product_id) REFERENCES Product(id),
                                  PRIMARY KEY (category_id, product_id)
);
