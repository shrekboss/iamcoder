CREATE TABLE SUBSCRIPTIONS
(
    ID            INTEGER PRIMARY KEY,
    PRODUCTID     VARCHAR(20),
    PACKAGEID     VARCHAR(20),
    MSISDN        VARCHAR(13),
    OPERATIONTIME TIMESTAMP,
    EFFECTIVEDATE TIMESTAMP,
    DUEDATE       TIMESTAMP,
    OPERATIONTYPE INTEGER
)