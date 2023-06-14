CREATE TABLE IF NOT EXISTS social_message (
    message_id SERIAL NOT NULL,
    context VARCHAR(65535),
    create_date_time TIMESTAMP(6),
    lang VARCHAR(255),
    origin VARCHAR(255),
    username VARCHAR(65535),
    negative_sentiment FLOAT,
    loc VARCHAR(255),
    PRIMARY KEY (message_id)
);

CREATE TABLE IF NOT EXISTS social_message_text (
    id SERIAL NOT NULL,
    message_id VARCHAR(255),
    text VARCHAR(65535),
    PRIMARY KEY (id)
);


