-- Создание таблицы жанров
CREATE TABLE IF NOT EXISTS genre (
    genre_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Создание таблицы MPA
CREATE TABLE IF NOT EXISTS mpa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rating VARCHAR(255) NOT NULL
);

-- Создание таблицы фильмов
CREATE TABLE IF NOT EXISTS film (
    film_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    release_date DATE,
    duration INT,
    mpa_id INT,
    FOREIGN KEY (mpa_id) REFERENCES mpa(id)
);

-- Создание таблицы пользователей
CREATE TABLE IF NOT EXISTS "user" (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    login VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    birthday DATE
);

-- Создание таблицы связей между фильмами и жанрами
CREATE TABLE IF NOT EXISTS film_genre (
    film_id BIGINT,
    genre_id INT,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES film(film_id),
    FOREIGN KEY (genre_id) REFERENCES genre(genre_id)
);

-- Создание таблицы дружбы
CREATE TABLE IF NOT EXISTS friendship (
    user_id INT NOT NULL,
    friend_id INT NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES "user"(user_id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES "user"(user_id) ON DELETE CASCADE
);

-- Создание таблицы лайков
CREATE TABLE IF NOT EXISTS likes (
    user_id INT,
    film_id BIGINT,
    PRIMARY KEY (user_id, film_id),
    FOREIGN KEY (user_id) REFERENCES "user"(user_id),
    FOREIGN KEY (film_id) REFERENCES film(film_id)
);