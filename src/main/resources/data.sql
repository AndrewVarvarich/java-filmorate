-- Заполнение таблицы MPA
INSERT INTO mpa (id, rating)
SELECT 1, 'G'
WHERE NOT EXISTS (SELECT 1 FROM mpa WHERE id = 1);

INSERT INTO mpa (id, rating)
SELECT 2, 'PG'
WHERE NOT EXISTS (SELECT 1 FROM mpa WHERE id = 2);

INSERT INTO mpa (id, rating)
SELECT 3, 'PG-13'
WHERE NOT EXISTS (SELECT 1 FROM mpa WHERE id = 3);

INSERT INTO mpa (id, rating)
SELECT 4, 'R'
WHERE NOT EXISTS (SELECT 1 FROM mpa WHERE id = 4);

INSERT INTO mpa (id, rating)
SELECT 5, 'NC-17'
WHERE NOT EXISTS (SELECT 1 FROM mpa WHERE id = 5);

-- Заполнение таблицы GENRE
INSERT INTO genre (genre_id, name)
SELECT 1, 'Комедия'
WHERE NOT EXISTS (SELECT 1 FROM genre WHERE genre_id = 1);

INSERT INTO genre (genre_id, name)
SELECT 2, 'Драма'
WHERE NOT EXISTS (SELECT 1 FROM genre WHERE genre_id = 2);

INSERT INTO genre (genre_id, name)
SELECT 3, 'Мультфильм'
WHERE NOT EXISTS (SELECT 1 FROM genre WHERE genre_id = 3);

INSERT INTO genre (genre_id, name)
SELECT 4, 'Триллер'
WHERE NOT EXISTS (SELECT 1 FROM genre WHERE genre_id = 4);

INSERT INTO genre (genre_id, name)
SELECT 5, 'Документальный'
WHERE NOT EXISTS (SELECT 1 FROM genre WHERE genre_id = 5);

INSERT INTO genre (genre_id, name)
SELECT 6, 'Боевик'
WHERE NOT EXISTS (SELECT 1 FROM genre WHERE genre_id = 6);

