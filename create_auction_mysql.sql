CREATE TABLE Participant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lastName VARCHAR(50),
    firstName VARCHAR(50),
    birthDate DATE
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
    reservePrice DOUBLE,
    restored BOOLEAN,
    info_id BIGINT,
    seller_id BIGINT,
    currency VARCHAR(5)
);

ALTER TABLE Info ADD fk_info_prod FOREIGN KEY (product_id) REFERENCES Product(id);

ALTER TABLE Product ADD fk_prod_info FOREIGN KEY (info_id) REFERENCES Info(id);

ALTER TABLE Product ADD FOREIGN KEY (seller_id) REFERENCES Participant(id);

CREATE TABLE product_category (
    category_id BIGINT,
    product_id BIGINT,
    FOREIGN KEY (category_id) REFERENCES Category(id),
    FOREIGN KEY (product_id) REFERENCES Product(id),
    PRIMARY KEY (category_id, product_id)
);
