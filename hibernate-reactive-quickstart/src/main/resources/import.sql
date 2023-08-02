INSERT INTO known_fruits(id, name) VALUES (1, 'Cherry');
INSERT INTO known_fruits(id, name) VALUES (2, 'Apple');
INSERT INTO known_fruits(id, name) VALUES (3, 'Banana');

INSERT INTO language(id, name, "value") VALUES (1, 'English (US)', 'en_US');
INSERT INTO language(id, name, "value") VALUES (2, 'English (UK)', 'en_UK');
INSERT INTO language(id, name, "value") VALUES (3, 'Español', 'es_ES');
INSERT INTO language(id, name, "value") VALUES (4, 'Italiano', 'it_IT');

INSERT INTO translation("language", class_name, class_pk, "key", "value") VALUES ('it_IT', 'org.acme.hibernate.reactive.Fruit', 1, 'name', 'ciliegia');
INSERT INTO translation("language", class_name, class_pk, "key", "value") VALUES ('it_IT', 'org.acme.hibernate.reactive.Fruit', 2, 'name', 'mela');
INSERT INTO translation("language", class_name, class_pk, "key", "value") VALUES ('en_UK', 'org.acme.hibernate.reactive.Fruit', 2, 'name', 'apple');
INSERT INTO translation("language", class_name, class_pk, "key", "value") VALUES ('en_US', 'org.acme.hibernate.reactive.Fruit', 2, 'name', 'apple');
--INSERT INTO translation("language", class_name, class_pk, "key", "value") VALUES ('it_IT', 'org.acme.hibernate.reactive.Fruit', 3, 'name', 'banana');