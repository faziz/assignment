DROP TABLE IF EXISTS address CASCADE;

CREATE TABLE address (
    ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ADDRESS1 VARCHAR(20) NOT NULL,
    ADDRESS2 VARCHAR(20),
    CITY VARCHAR(20) NOT NULL,
    STATE VARCHAR(20)
) ENGINE = innodb;


DROP TABLE IF EXISTS user CASCADE;
CREATE TABLE user (
    ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    USERNAME VARCHAR(20) NOT NULL UNIQUE,
    PASSWORD VARCHAR(20) NOT NULL,
    FIRSTNAME VARCHAR(20) NOT NULL,
    LASTNAME VARCHAR(20) NOT NULL,
    MIDDLENAME VARCHAR(20),
    APITOKEN VARCHAR(20) NOT NULL,
    ADDRESS_ID INT
) ENGINE = innodb;

ALTER TABLE user ADD CONSTRAINT user_address_fk FOREIGN KEY (ADDRESS_ID) REFERENCES address(ID) ON UPDATE NO ACTION;

# Reference data for default admin.
INSERT INTO address (ADDRESS1, ADDRESS2, CITY, `STATE`) VALUES ('address1', 'address2', 'Karachi', 'Sindh');
INSERT INTO user (USERNAME, PASSWORD, FIRSTNAME, LASTNAME, MIDDLENAME, APITOKEN, ADDRESS_ID) VALUES ('admin', 'adminpwd', 'admin', 'admin', 'admin', 'token', 1);
