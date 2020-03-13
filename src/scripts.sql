create DATABASE phrases;
drop table example;
drop table word;
drop table rule;


create TABLE word
(
    Id SERIAL PRIMARY KEY,
    value CHARACTER VARYING(30) UNIQUE NOT NULL
);

create TABLE rule
(
    Id SERIAL PRIMARY KEY,
    value text UNIQUE NOT NULL
);

create TABLE example (
    id SERIAL PRIMARY KEY,
    word_id integer,
	russian text,
    english text,
	sound character varying(256),
	rule_id integer,
    FOREIGN KEY (word_id) REFERENCES word (id)
        ON delete CASCADE
        ON update CASCADE
    FOREIGN KEY (rule_id) REFERENCES rule (id)
        ON delete SET NULL
        ON update CASCADE
);

select word.value, example.russian, example.english, example.sound, rule.value from word
join example on word.id = example.word_id
join rule on example.rule_id = rule.id;