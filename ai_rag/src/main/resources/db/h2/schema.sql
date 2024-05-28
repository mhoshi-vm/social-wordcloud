CREATE SEQUENCE vector_db_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS public.vector_db (
    id integer NOT NULL,
    message_id VARCHAR(255),
    vector VARCHAR(255),
    PRIMARY KEY (id)
);

