CREATE TABLE IF NOT EXISTS social_message (
    message_id VARCHAR(255) NOT NULL,
    context VARCHAR(65535),
    create_date_time TIMESTAMP(6),
    lang VARCHAR(255),
    origin VARCHAR(255),
    username VARCHAR(255),
    negative_sentiment FLOAT,
    PRIMARY KEY (message_id)
);

CREATE TABLE IF NOT EXISTS social_message_text (
    id INTEGER NOT NULL,
    message_id VARCHAR(255),
    text VARCHAR(255),
    PRIMARY KEY (id)
);


