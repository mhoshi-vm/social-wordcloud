CREATE SEQUENCE IF NOT EXISTS social_message_text_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS social_message (
    message_id VARCHAR(255) NOT NULL,
    context CLOB,
    create_date_time TIMESTAMP(6),
    lang VARCHAR(255),
    origin VARCHAR(255),
    username VARCHAR(255), PRIMARY KEY (message_id)
);

CREATE TABLE IF NOT EXISTS social_message_text (
    id INTEGER NOT NULL,
    message_id VARCHAR(255),
    text VARCHAR(255),
    PRIMARY KEY (id)
);

