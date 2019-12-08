CREATE DATABASE phrases;
DROP TABLE example;
DROP TABLE word;
DROP TABLE rule;


CREATE TABLE word
(
    Id SERIAL PRIMARY KEY,
    value CHARACTER VARYING(30) UNIQUE NOT NULL
);

CREATE TABLE rule
(
    Id SERIAL PRIMARY KEY,
    value text UNIQUE NOT NULL
);

CREATE TABLE example (
    id SERIAL PRIMARY KEY,
    word_id integer,
	russian text,
    english text,
	sound character varying(256),
	rule_id integer,
    FOREIGN KEY (word_id) REFERENCES word (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
    FOREIGN KEY (rule_id) REFERENCES rule (id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);