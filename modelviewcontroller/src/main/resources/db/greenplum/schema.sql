CREATE EXTENSION vector;
CREATE EXTENSION pgml;

CREATE SEQUENCE social_message_text_seq START WITH 100000 INCREMENT BY 50;
CREATE SEQUENCE social_message_image_seq START WITH 1000 INCREMENT BY 50;
CREATE SEQUENCE vector_db_seq START WITH 1 INCREMENT BY 50;

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


CREATE TABLE social_message_image (
   id INTEGER NOT NULL,
   message_id VARCHAR(255),
   image BYTEA,
   PRIMARY KEY (id)
);

CREATE TABLE public.vector_db (
    id integer NOT NULL,
    message_id VARCHAR(255),
    vector VECTOR(384),
    PRIMARY KEY (id)
);