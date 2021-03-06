CREATE TABLE config_entries (
    ID 					BINARY(16) NOT NULL,
    PROPERTY 			VARCHAR(255) NOT NULL,
    VALUE 				VARCHAR(255) NOT NULL,
    PRIMARY KEY (ID , PROPERTY)
);

ALTER TABLE calls ADD COLUMN CALL_TYPE          VARCHAR(30)  NULL;
ALTER TABLE calls ADD COLUMN NO_CONN_DEVICE_ID  VARCHAR(255) NULL;
ALTER TABLE calls ADD COLUMN CALLING_TO         VARCHAR(255) NULL;
