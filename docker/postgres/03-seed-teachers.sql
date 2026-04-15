\c teachers_db;

CREATE TABLE language (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE teachers (
    user_id BIGINT PRIMARY KEY,
    bio VARCHAR(200),
    price_per_hour DOUBLE PRECISION NOT NULL,
    rating DOUBLE PRECISION NOT NULL
);

CREATE TABLE teacher_teaching_languages (
    teacher_id BIGINT NOT NULL,
    language_id BIGINT NOT NULL,
    PRIMARY KEY (teacher_id, language_id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(user_id),
    FOREIGN KEY (language_id) REFERENCES language(id)
);

CREATE TABLE teacher_spoken_languages (
    teacher_id BIGINT NOT NULL,
    language_id BIGINT NOT NULL,
    PRIMARY KEY (teacher_id, language_id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(user_id),
    FOREIGN KEY (language_id) REFERENCES language(id)
);

CREATE TABLE availability_slots (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL
);

INSERT INTO language (name) VALUES
('Inglés'), ('Español'), ('Francés'), ('Alemán'), ('Italiano'), ('Japonés');

INSERT INTO teachers (user_id, bio, price_per_hour, rating) VALUES
(1, 'Profesor nativo de español con 10 años de experiencia.', 25.00, 4.8),
(2, 'Profesora de inglés certificada. Especializada en inglés de negocios.', 30.00, 4.9),
(3, 'Profesor francés nativo de París.', 28.00, 4.7),
(4, 'Profesor alemán con metodología estructurada.', 32.00, 4.6),
(5, 'Profesora italiana nativa. Cultura y gastronomía en cada clase.', 26.00, 4.8),
(6, 'Profesor de japonés. Hiragana, Katakana y Kanji desde cero.', 35.00, 4.9);

INSERT INTO teacher_teaching_languages (teacher_id, language_id) VALUES
(1, 2), (2, 1), (3, 3), (4, 4), (5, 5), (6, 6);

INSERT INTO teacher_spoken_languages (teacher_id, language_id) VALUES
(1, 2), (1, 1),
(2, 1), (2, 2),
(3, 3), (3, 2),
(4, 4), (4, 2),
(5, 5), (5, 2),
(6, 6), (6, 2);

INSERT INTO availability_slots (teacher_id, start_time)
SELECT t.teacher_id, gs.slot
FROM (VALUES (1), (2), (3), (4), (5), (6)) AS t(teacher_id)
CROSS JOIN LATERAL (
    SELECT generate_series(
        date_trunc('day', NOW() + interval '2 days') + interval '9 hours',
        date_trunc('day', NOW() + interval '30 days') + interval '9 hours',
        interval '1 day'
    ) AS slot
    UNION ALL
    SELECT generate_series(
        date_trunc('day', NOW() + interval '2 days') + interval '11 hours',
        date_trunc('day', NOW() + interval '30 days') + interval '11 hours',
        interval '1 day'
    )
    UNION ALL
    SELECT generate_series(
        date_trunc('day', NOW() + interval '2 days') + interval '16 hours',
        date_trunc('day', NOW() + interval '30 days') + interval '16 hours',
        interval '1 day'
    )
    UNION ALL
    SELECT generate_series(
        date_trunc('day', NOW() + interval '2 days') + interval '18 hours',
        date_trunc('day', NOW() + interval '30 days') + interval '18 hours',
        interval '1 day'
    )
) AS gs;