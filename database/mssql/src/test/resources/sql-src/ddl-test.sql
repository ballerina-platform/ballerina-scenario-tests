CREATE TABLE DDL_TEST_DROP_TABLE
(
    X INT,
    Y VARCHAR(20)
);
/
CREATE TABLE DDL_TEST_ALTER_TABLE
(
    X INT PRIMARY KEY,
    Y VARCHAR(20),
    Z DATETIME
);
/
CREATE TABLE DDL_TEST_PROC_TABLE
(
    X INT PRIMARY KEY,
    Y VARCHAR(20)
);
/
CREATE PROCEDURE DDL_TEST_DROPPING_PROC AS
BEGIN
    SELECT X, Y FROM DDL_TEST_PROC_TABLE;
END;
/
CREATE PROCEDURE DDL_TEST_ALTER_PROC AS
BEGIN
    SELECT * FROM DDL_TEST_ALTER_TABLE;
END;
/
CREATE INDEX DDL_TEST_DROP_INDEX ON DDL_TEST_ALTER_TABLE (X);
/
