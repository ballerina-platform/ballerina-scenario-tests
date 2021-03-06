CREATE TABLE TRX_TEST(ID INT, VAL VARCHAR(20));
/
CREATE TABLE TRX_TEST_AUTO_GEN_KEY(ID INT, VAL VARCHAR(20), AUTOGEN VARCHAR(20));
/
CREATE TABLE TRX_TEST_AUTO_GEN_KEY_SEQ(id INT NOT NULL AUTO_INCREMENT PRIMARY KEY);
/
CREATE TRIGGER TG_TRX_TEST_AUTO_GEN_KEY_INSERT
BEFORE INSERT ON TRX_TEST_AUTO_GEN_KEY
FOR EACH ROW
BEGIN
 INSERT INTO TRX_TEST_AUTO_GEN_KEY_SEQ VALUES (NULL);
 SET NEW.AUTOGEN = CONCAT('id', LAST_INSERT_ID());
END
/